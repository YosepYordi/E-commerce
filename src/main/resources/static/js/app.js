/**
 * Juguetería Mágica - Script Principal Frontend
 * Consumo de Servicios REST Spring Boot mediante Fetch API
 * Módulo de Administración y Gestión de Juguetes
 */

document.addEventListener('DOMContentLoaded', () => {

    // --- State Management ---
    let currentCategory = 'ALL';
    let searchQuery = '';

    // --- Auth Token (JWT) ---
    // El token se guarda en sessionStorage tras el login del administrador
    // --- Auth & Security ---
    function getAuthToken() {
        return sessionStorage.getItem('adminToken') || null;
    }

    function getAdminHeaders() {
        const token = getAuthToken();
        const headers = { 'Content-Type': 'application/json' };
        if (token) headers['Authorization'] = `Bearer ${token}`;
        return headers;
    }

    function getCsrfToken() {
        const match = document.cookie.match(new RegExp('(^| )XSRF-TOKEN=([^;]+)'));
        return match ? decodeURIComponent(match[2]) : null;
    }

    function updateAdminUI() {
        const isAdmin = !!getAuthToken();
        const btnAdmin = document.getElementById('btn-open-admin');
        const btnLogin = document.getElementById('btn-open-login');
        const btnLogout = document.getElementById('btn-logout-admin');

        if (btnAdmin) btnAdmin.classList.toggle('hidden', !isAdmin);
        if (btnLogin) btnLogin.classList.toggle('hidden', isAdmin);
        if (btnLogout) btnLogout.classList.toggle('hidden', !isAdmin);

        document.querySelectorAll('.admin-card-actions').forEach(el => {
            el.style.display = isAdmin ? 'flex' : 'none';
        });
    }

    // --- DOM Elements ---
    const productsGrid = document.getElementById('products-grid');
    const resultsCount = document.getElementById('results-count');
    const noResults = document.getElementById('no-results');
    const searchInput = document.getElementById('search-input');
    const searchBtn = document.getElementById('search-btn');
    const categoriesBar = document.getElementById('categories-bar');
    const resetFiltersBtn = document.getElementById('reset-filters-btn');

    // Cart Elements
    const btnOpenCart = document.getElementById('btn-open-cart');
    const btnCloseCart = document.getElementById('btn-close-cart');
    const cartDrawer = document.getElementById('cart-drawer');
    const cartOverlay = document.getElementById('cart-overlay');
    const cartCountBadge = document.getElementById('cart-count-badge');
    const cartItemsList = document.getElementById('cart-items-list');
    const cartTotalPrice = document.getElementById('cart-total-price');
    const cartTotalItems = document.getElementById('cart-total-items');
    const btnClearCart = document.getElementById('btn-clear-cart');
    const btnCheckout = document.getElementById('btn-checkout');

    // Login Modal Elements
    const btnOpenLogin = document.getElementById('btn-open-login');
    const btnLogoutAdmin = document.getElementById('btn-logout-admin');
    const btnCloseLogin = document.getElementById('btn-close-login');
    const btnCancelLogin = document.getElementById('btn-cancel-login');
    const loginModal = document.getElementById('login-modal');
    const loginForm = document.getElementById('login-form');

    // Admin Modal Elements
    const btnOpenAdmin = document.getElementById('btn-open-admin');
    const btnCloseModal = document.getElementById('btn-close-modal');
    const btnCancelModal = document.getElementById('btn-cancel-modal');
    const adminModal = document.getElementById('admin-modal');
    const productForm = document.getElementById('product-form');

    // Toast Container
    const toastContainer = document.getElementById('toast-container');

    // --- Initial Initialization ---
    init();

    function init() {
        updateAdminUI();
        loadProducts();
        loadCart();
        setupEventListeners();
    }

    // --- Event Listeners Setup ---
    function setupEventListeners() {
        // Search Events
        searchBtn.addEventListener('click', () => {
            searchQuery = searchInput.value.trim();
            loadProducts();
        });

        searchInput.addEventListener('keyup', (e) => {
            if (e.key === 'Enter') {
                searchQuery = searchInput.value.trim();
                loadProducts();
            }
        });

        // Category Filter Pills
        categoriesBar.addEventListener('click', (e) => {
            const btn = e.target.closest('.cat-pill');
            if (!btn) return;

            document.querySelectorAll('.cat-pill').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            currentCategory = btn.dataset.category;
            loadProducts();
        });

        resetFiltersBtn.addEventListener('click', () => {
            currentCategory = 'ALL';
            searchQuery = '';
            searchInput.value = '';
            document.querySelectorAll('.cat-pill').forEach(b => {
                b.classList.toggle('active', b.dataset.category === 'ALL');
            });
            loadProducts();
        });

        // Drawer Controls
        btnOpenCart.addEventListener('click', openCartDrawer);
        btnCloseCart.addEventListener('click', closeCartDrawer);
        cartOverlay.addEventListener('click', closeCartDrawer);

        // Cart Action Buttons
        btnClearCart.addEventListener('click', handleClearCart);
        btnCheckout.addEventListener('click', handleCheckout);

        // Login Modal Controls
        if (btnOpenLogin) {
            btnOpenLogin.addEventListener('click', () => {
                loginForm.reset();
                loginModal.classList.remove('hidden');
            });
        }
        if (btnCloseLogin) btnCloseLogin.addEventListener('click', () => loginModal.classList.add('hidden'));
        if (btnCancelLogin) btnCancelLogin.addEventListener('click', () => loginModal.classList.add('hidden'));
        if (btnLogoutAdmin) btnLogoutAdmin.addEventListener('click', handleLogoutAdmin);
        if (loginForm) loginForm.addEventListener('submit', handleLoginSubmit);

        // Admin Modal Controls
        btnOpenAdmin.addEventListener('click', () => {
            productForm.reset();
            document.getElementById('product-id').value = '';
            document.getElementById('modal-title').textContent = 'Agregar Nuevo Juguete';
            adminModal.classList.remove('hidden');
        });
        btnCloseModal.addEventListener('click', () => adminModal.classList.add('hidden'));
        btnCancelModal.addEventListener('click', () => adminModal.classList.add('hidden'));
        productForm.addEventListener('submit', handleSaveProduct);
    }

    // ==========================================================================
    // API REST - Products Operations (Fetch API)
    // ==========================================================================

    async function loadProducts() {
        try {
            productsGrid.innerHTML = `
                <div style="grid-column: 1/-1; text-align: center; padding: 50px;">
                    <i class="fa-solid fa-spinner fa-spin" style="font-size: 2rem; color: var(--mint-primary);"></i>
                    <p style="margin-top: 12px; color: var(--text-muted); font-weight: 600;">Cargando juguetes...</p>
                </div>`;

            let url = '/api/products';
            const params = new URLSearchParams();

            if (currentCategory && currentCategory !== 'ALL') {
                params.append('categoria', currentCategory);
            }
            if (searchQuery) {
                params.append('query', searchQuery);
            }

            if ([...params].length > 0) {
                url += '?' + params.toString();
            }

            const response = await fetch(url);
            if (!response.ok) throw new Error('Error al cargar la lista de productos');

            const products = await response.json();
            renderProducts(products);
        } catch (error) {
            console.error('Fetch products error:', error);
            showToast('No se pudieron cargar los juguetes del servidor', 'error');
            productsGrid.innerHTML = '';
        }
    }

    function renderProducts(products) {
        resultsCount.textContent = `Mostrando ${products.length} juguete(s) en catálogo`;

        if (!products || products.length === 0) {
            productsGrid.classList.add('hidden');
            noResults.classList.remove('hidden');
            return;
        }

        const isAdmin = !!getAuthToken();

        noResults.classList.add('hidden');
        productsGrid.classList.remove('hidden');

        productsGrid.innerHTML = products.map(product => `
            <div class="product-card">
                <div class="card-img-wrapper">
                    <img src="${product.imagenUrl || 'https://images.unsplash.com/photo-1558060370-d644479be967?w=500'}"
                         alt="${escapeHtml(product.nombre)}" class="card-img"
                         onerror="this.src='https://images.unsplash.com/photo-1558060370-d644479be967?w=500'">
                    <span class="card-category-badge">${escapeHtml(product.categoria)}</span>
                    <div class="admin-card-actions" style="display: ${isAdmin ? 'flex' : 'none'};">
                        <button class="btn-card-action edit" onclick="handleEditProduct(${product.id})" title="Editar juguete">
                            <i class="fa-solid fa-pen-to-square"></i>
                        </button>
                        <button class="btn-card-action delete" onclick="handleDeleteProduct(${product.id})" title="Eliminar juguete">
                            <i class="fa-solid fa-trash-can"></i>
                        </button>
                    </div>
                </div>
                <div class="card-body">
                    <h3 class="card-title">${escapeHtml(product.nombre)}</h3>
                    <p class="card-description">${escapeHtml(product.descripcion || 'Sin descripción detallada.')}</p>
                    <div class="card-footer">
                        <div class="card-price-box">
                            <span class="price-label">Precio</span>
                            <span class="card-price">S/ ${parseFloat(product.precio).toFixed(2)}</span>
                        </div>
                        <button class="btn-add-cart"
                                onclick="handleAddToCart(${product.id})"
                                ${product.stock <= 0 ? 'disabled' : ''}>
                            <i class="fa-solid fa-cart-plus"></i>
                            ${product.stock > 0 ? 'Añadir' : 'Agotado'}
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
    }

    // ==========================================================================
    // API REST - Shopping Cart Operations (Fetch API)
    // ==========================================================================

    async function loadCart() {
        try {
            const response = await fetch('/api/cart', {
                credentials: 'include'
            });
            if (!response.ok) throw new Error('Error al consultar el carrito');

            const cart = await response.json();
            updateCartUI(cart);
        } catch (error) {
            console.error('Fetch cart error:', error);
        }
    }

    function getCartHeaders(includeContentType = false) {
        const headers = {};
        if (includeContentType) headers['Content-Type'] = 'application/json';
        const csrf = getCsrfToken();
        if (csrf) headers['X-XSRF-TOKEN'] = csrf;
        return headers;
    }

    window.handleAddToCart = async function(productId) {
        try {
            const response = await fetch('/api/cart/add', {
                method: 'POST',
                credentials: 'include',
                headers: getCartHeaders(true),
                body: JSON.stringify({ productId, cantidad: 1 })
            });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.message || 'Error al agregar al carrito');
            }

            const updatedCart = await response.json();
            updateCartUI(updatedCart);
            showToast('¡Juguete añadido al carrito de compras!', 'success');

        } catch (error) {
            showToast(error.message, 'error');
        }
    };

    async function updateQuantity(productId, cantidad) {
        try {
            const response = await fetch('/api/cart/update', {
                method: 'PUT',
                credentials: 'include',
                headers: getCartHeaders(true),
                body: JSON.stringify({ productId, cantidad })
            });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.message || 'Error al actualizar cantidad');
            }

            const updatedCart = await response.json();
            updateCartUI(updatedCart);
        } catch (error) {
            showToast(error.message, 'error');
        }
    }

    window.handleRemoveCartItem = async function(productId) {
        try {
            const response = await fetch(`/api/cart/items/${productId}`, {
                method: 'DELETE',
                credentials: 'include',
                headers: getCartHeaders(false)
            });

            if (!response.ok) throw new Error('Error al eliminar item del carrito');

            const updatedCart = await response.json();
            updateCartUI(updatedCart);
            showToast('Juguete eliminado del carrito', 'success');
        } catch (error) {
            showToast(error.message, 'error');
        }
    };

    async function handleClearCart() {
        if (!confirm('¿Estás seguro de vaciar el carrito?')) return;

        try {
            const response = await fetch('/api/cart', {
                method: 'DELETE',
                credentials: 'include',
                headers: getCartHeaders(false)
            });
            if (!response.ok) throw new Error('Error al vaciar el carrito');

            loadCart();
            showToast('Carrito vaciado', 'success');
        } catch (error) {
            showToast(error.message, 'error');
        }
    }

    async function handleCheckout() {
        try {
            const response = await fetch('/api/cart/checkout', {
                method: 'POST',
                credentials: 'include',
                headers: getCartHeaders(false)
            });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.message || 'No se pudo procesar la compra');
            }

            const summary = await response.json();
            loadCart();
            loadProducts();
            closeCartDrawer();

            alert(`🎉 ¡Compra realizada con éxito!\n\nHas adquirido ${summary.totalItems} juguete(s) por un total de S/ ${parseFloat(summary.total).toFixed(2)}.\n\n¡Gracias por tu compra en Juguetería Mágica!`);
            showToast('¡Gracias por tu compra! Pedido registrado', 'success');

        } catch (error) {
            showToast(error.message, 'error');
        }
    }

    function updateCartUI(cart) {
        cartCountBadge.textContent = cart.totalItems || 0;
        cartTotalItems.textContent = cart.totalItems || 0;
        cartTotalPrice.textContent = `S/ ${parseFloat(cart.total || 0).toFixed(2)}`;

        if (!cart.items || cart.items.length === 0) {
            cartItemsList.innerHTML = `
                <div style="text-align: center; padding: 40px 0; color: var(--text-muted);">
                    <i class="fa-solid fa-basket-shopping" style="font-size: 2.8rem; margin-bottom: 12px; opacity: 0.3;"></i>
                    <p style="font-weight: 700;">Tu carrito está vacío</p>
                    <small>¡Explora el catálogo y agrega tus juguetes favoritos!</small>
                </div>`;
            btnCheckout.disabled = true;
            btnClearCart.disabled = true;
            return;
        }

        btnCheckout.disabled = false;
        btnClearCart.disabled = false;

        cartItemsList.innerHTML = cart.items.map(item => `
            <div class="cart-item">
                <img src="${item.product.imagenUrl || 'https://images.unsplash.com/photo-1558060370-d644479be967?w=500'}"
                     alt="${escapeHtml(item.product.nombre)}" class="cart-item-img">
                <div class="cart-item-info">
                    <div class="cart-item-title">${escapeHtml(item.product.nombre)}</div>
                    <div class="cart-item-price">S/ ${parseFloat(item.product.precio).toFixed(2)} c/u</div>
                    <div class="qty-controls">
                        <button class="btn-qty" onclick="changeQuantity(${item.product.id}, ${item.cantidad - 1})">-</button>
                        <span class="qty-val">${item.cantidad}</span>
                        <button class="btn-qty" onclick="changeQuantity(${item.product.id}, ${item.cantidad + 1})">+</button>
                    </div>
                </div>
                <div style="text-align: right;">
                    <div style="font-weight: 800; font-size: 0.95rem; color: var(--coral-accent);">
                        S/ ${parseFloat(item.subtotal).toFixed(2)}
                    </div>
                    <button class="btn-remove-item" onclick="handleRemoveCartItem(${item.product.id})" title="Eliminar del carrito">
                        <i class="fa-solid fa-trash-can"></i>
                    </button>
                </div>
            </div>
        `).join('');
    }

    window.changeQuantity = function(productId, newQty) {
        updateQuantity(productId, newQty);
    };

    // ==========================================================================
    // API REST - Admin Authentication & Operations (Login, JWT, Product CRUD)
    // ==========================================================================

    async function handleLoginSubmit(e) {
        e.preventDefault();

        const usernameInput = document.getElementById('login-username');
        const passwordInput = document.getElementById('login-password');

        const username = usernameInput ? usernameInput.value.trim() : '';
        const password = passwordInput ? passwordInput.value : '';

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            if (!response.ok) {
                const errData = await response.json().catch(() => ({}));
                throw new Error(errData.message || 'Credenciales de administrador inválidas');
            }

            const data = await response.json();
            const token = data.token || data.accessToken;
            sessionStorage.setItem('adminToken', token);

            if (loginModal) loginModal.classList.add('hidden');
            if (loginForm) loginForm.reset();
            updateAdminUI();
            loadProducts();
            showToast('¡Sesión de administrador iniciada con éxito!', 'success');
        } catch (error) {
            showToast(error.message, 'error');
        }
    }

    function handleLogoutAdmin() {
        sessionStorage.removeItem('adminToken');
        updateAdminUI();
        loadProducts();
        showToast('Sesión de administrador cerrada', 'info');
    }

    async function handleSaveProduct(e) {
        e.preventDefault();

        const productId = document.getElementById('product-id').value;
        const productData = {
            nombre: document.getElementById('product-nombre').value.trim(),
            categoria: document.getElementById('product-categoria').value,
            precio: parseFloat(document.getElementById('product-precio').value),
            stock: parseInt(document.getElementById('product-stock').value),
            imagenUrl: document.getElementById('product-imagen').value.trim() || 'https://images.unsplash.com/photo-1558060370-d644479be967?w=500',
            descripcion: document.getElementById('product-descripcion').value.trim()
        };

        try {
            const url = productId ? `/api/products/${productId}` : '/api/products';
            const method = productId ? 'PUT' : 'POST';

            const response = await fetch(url, {
                method: method,
                headers: getAdminHeaders(),
                body: JSON.stringify(productData)
            });

            if (!response.ok) {
                if (response.status === 401 || response.status === 403) {
                    sessionStorage.removeItem('adminToken');
                    updateAdminUI();
                    adminModal.classList.add('hidden');
                    if (loginModal) loginModal.classList.remove('hidden');
                    throw new Error('Sesión no autorizada o token expirado. Por favor inicie sesión.');
                }
                const errData = await response.json().catch(() => ({}));
                throw new Error(errData.message || 'Error al guardar el producto');
            }

            adminModal.classList.add('hidden');
            productForm.reset();
            document.getElementById('product-id').value = '';
            loadProducts();
            showToast(productId ? '¡Juguete actualizado exitosamente!' : '¡Nuevo juguete agregado al catálogo!', 'success');

        } catch (error) {
            showToast(error.message, 'error');
        }
    }

    window.handleEditProduct = async function(id) {
        try {
            const response = await fetch(`/api/products/${id}`);
            if (!response.ok) throw new Error('No se pudo cargar la información del juguete');

            const product = await response.json();

            document.getElementById('product-id').value = product.id;
            document.getElementById('product-nombre').value = product.nombre;
            document.getElementById('product-categoria').value = product.categoria;
            document.getElementById('product-precio').value = product.precio;
            document.getElementById('product-stock').value = product.stock;
            document.getElementById('product-imagen').value = product.imagenUrl || '';
            document.getElementById('product-descripcion').value = product.descripcion || '';

            document.getElementById('modal-title').textContent = 'Editar Juguete';
            adminModal.classList.remove('hidden');
        } catch (error) {
            showToast(error.message, 'error');
        }
    };

    window.handleDeleteProduct = async function(id) {
        if (!confirm('¿Estás seguro de que deseas eliminar este juguete del catálogo?')) return;

        try {
            const response = await fetch(`/api/products/${id}`, {
                method: 'DELETE',
                headers: getAdminHeaders()
            });

            if (!response.ok) {
                if (response.status === 401 || response.status === 403) {
                    sessionStorage.removeItem('adminToken');
                    updateAdminUI();
                    if (loginModal) loginModal.classList.remove('hidden');
                    throw new Error('Sesión no autorizada o token expirado. Por favor inicie sesión.');
                }
                throw new Error('Error al eliminar el producto');
            }

            loadProducts();
            showToast('Juguete eliminado del catálogo', 'success');
        } catch (error) {
            showToast(error.message, 'error');
        }
    };

    // --- Drawer Helpers ---
    function openCartDrawer() {
        cartDrawer.classList.add('active');
        cartOverlay.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }

    function closeCartDrawer() {
        cartDrawer.classList.remove('active');
        cartOverlay.classList.add('hidden');
        document.body.style.overflow = '';
    }

    // --- Toast Notifications ---
    function showToast(message, type = 'success') {
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `
            <i class="fa-solid ${type === 'success' ? 'fa-circle-check' : 'fa-circle-exclamation'}"></i>
            <span>${escapeHtml(message)}</span>
        `;
        toastContainer.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateY(10px)';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }

    function escapeHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }
});
