/**
 * NEXUS Store & Administration Frontend Script
 * Consumo de Servicios REST Spring Boot mediante Fetch API
 * Módulo de Administración y Gestión de Productos
 */

document.addEventListener('DOMContentLoaded', () => {

    // --- State Management ---
    let currentCategory = 'ALL';
    let searchQuery = '';

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

        // Admin Modal Controls
        btnOpenAdmin.addEventListener('click', () => {
            productForm.reset();
            document.getElementById('product-id').value = '';
            document.getElementById('modal-title').textContent = 'Agregar Nuevo Producto';
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
                    <i class="fa-solid fa-circle-notch fa-spin" style="font-size: 2rem; color: var(--primary);"></i>
                    <p style="margin-top: 12px; color: var(--text-secondary); font-weight: 500;">Cargando catálogo de productos...</p>
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
            if (!response.ok) throw new Error('Error al consultar el catálogo de productos');

            const products = await response.json();
            renderProducts(products);

        } catch (error) {
            console.error('Fetch products error:', error);
            showToast('No se pudieron recuperar los productos del servidor', 'error');
            productsGrid.innerHTML = '';
        }
    }

    function renderProducts(products) {
        resultsCount.textContent = `Mostrando ${products.length} producto(s) en catálogo`;

        if (!products || products.length === 0) {
            productsGrid.classList.add('hidden');
            noResults.classList.remove('hidden');
            return;
        }

        noResults.classList.add('hidden');
        productsGrid.classList.remove('hidden');

        productsGrid.innerHTML = products.map(product => `
            <div class="product-card">
                <div class="card-img-wrapper">
                    <img src="${product.imagenUrl || 'https://images.unsplash.com/photo-1558060370-d644479be967?w=500'}" 
                         alt="${escapeHtml(product.nombre)}" class="card-img"
                         onerror="this.src='https://images.unsplash.com/photo-1558060370-d644479be967?w=500'">
                    <span class="card-category-badge">${escapeHtml(product.categoria)}</span>
                    <div class="admin-card-actions">
                        <button class="btn-card-action edit" onclick="handleEditProduct(${product.id})" title="Editar producto">
                            <i class="fa-solid fa-pen-to-square"></i>
                        </button>
                        <button class="btn-card-action delete" onclick="handleDeleteProduct(${product.id})" title="Eliminar producto">
                            <i class="fa-solid fa-trash-can"></i>
                        </button>
                    </div>
                </div>
                <div class="card-body">
                    <h3 class="card-title">${escapeHtml(product.nombre)}</h3>
                    <p class="card-description">${escapeHtml(product.descripcion || 'Sin especificaciones detalladas.')}</p>
                    <div class="card-footer">
                        <div class="card-price-box">
                            <span class="price-label">Precio</span>
                            <span class="card-price">S/ ${parseFloat(product.precio).toFixed(2)}</span>
                        </div>
                        <button class="btn-add-cart" 
                                onclick="handleAddToCart(${product.id})" 
                                ${product.stock <= 0 ? 'disabled' : ''}>
                            <i class="fa-solid fa-cart-plus"></i>
                            ${product.stock > 0 ? 'Agregar' : 'Agotado'}
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
            const response = await fetch('/api/cart');
            if (!response.ok) throw new Error('Error al consultar el carrito');

            const cart = await response.json();
            updateCartUI(cart);
        } catch (error) {
            console.error('Fetch cart error:', error);
        }
    }

    window.handleAddToCart = async function(productId) {
        try {
            const response = await fetch('/api/cart/add', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ productId, cantidad: 1 })
            });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.message || 'Error al agregar item al carrito');
            }

            const updatedCart = await response.json();
            updateCartUI(updatedCart);
            showToast('Producto agregado al carrito de compras', 'success');

        } catch (error) {
            showToast(error.message, 'error');
        }
    };

    async function updateQuantity(productId, cantidad) {
        try {
            const response = await fetch('/api/cart/update', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
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
                method: 'DELETE'
            });

            if (!response.ok) throw new Error('Error al eliminar producto del carrito');

            const updatedCart = await response.json();
            updateCartUI(updatedCart);
            showToast('Producto eliminado del carrito', 'success');
        } catch (error) {
            showToast(error.message, 'error');
        }
    };

    async function handleClearCart() {
        if (!confirm('¿Desea vaciar el contenido del carrito de compras?')) return;

        try {
            const response = await fetch('/api/cart', { method: 'DELETE' });
            if (!response.ok) throw new Error('Error al vaciar el carrito');

            loadCart();
            showToast('Carrito de compras vaciado', 'success');
        } catch (error) {
            showToast(error.message, 'error');
        }
    }

    async function handleCheckout() {
        try {
            const response = await fetch('/api/cart/checkout', { method: 'POST' });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.message || 'No se pudo procesar la orden');
            }

            const summary = await response.json();
            loadCart();
            loadProducts(); // Refresh stock
            closeCartDrawer();

            alert(`✅ ¡Orden procesada exitosamente!\n\nSe procesó la adquisición de ${summary.totalItems} producto(s) por un total de S/ ${parseFloat(summary.total).toFixed(2)}.\n\nGracias por su compra en NEXUS Store.`);
            showToast('¡Orden registrada exitosamente en el sistema!', 'success');

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
                    <i class="fa-solid fa-bag-shopping" style="font-size: 2.5rem; margin-bottom: 12px; opacity: 0.3;"></i>
                    <p style="font-weight: 600;">El carrito está vacío</p>
                    <small>Seleccione productos del catálogo para agregarlos.</small>
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
                    <div style="font-weight: 800; font-size: 0.92rem; color: var(--primary);">
                        S/ ${parseFloat(item.subtotal).toFixed(2)}
                    </div>
                    <button class="btn-remove-item" onclick="handleRemoveCartItem(${item.product.id})" title="Eliminar item">
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
    // API REST - Admin Product Operations (Create, Update, Delete)
    // ==========================================================================

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
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(productData)
            });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.message || 'Error al guardar el registro');
            }

            adminModal.classList.add('hidden');
            productForm.reset();
            document.getElementById('product-id').value = '';
            loadProducts();
            showToast(productId ? 'Producto actualizado en el inventario' : 'Nuevo producto registrado exitosamente', 'success');

        } catch (error) {
            showToast(error.message, 'error');
        }
    }

    window.handleEditProduct = async function(id) {
        try {
            const response = await fetch(`/api/products/${id}`);
            if (!response.ok) throw new Error('No se pudo recuperar la información del producto');

            const product = await response.json();

            document.getElementById('product-id').value = product.id;
            document.getElementById('product-nombre').value = product.nombre;
            document.getElementById('product-categoria').value = product.categoria;
            document.getElementById('product-precio').value = product.precio;
            document.getElementById('product-stock').value = product.stock;
            document.getElementById('product-imagen').value = product.imagenUrl || '';
            document.getElementById('product-descripcion').value = product.descripcion || '';

            document.getElementById('modal-title').textContent = 'Editar Producto de Inventario';
            adminModal.classList.remove('hidden');
        } catch (error) {
            showToast(error.message, 'error');
        }
    };

    window.handleDeleteProduct = async function(id) {
        if (!confirm('¿Confirma que desea eliminar este producto del sistema de inventario?')) return;

        try {
            const response = await fetch(`/api/products/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) throw new Error('Error al eliminar el registro');

            loadProducts();
            showToast('Registro eliminado del sistema', 'success');
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
            <i class="fa-solid ${type === 'success' ? 'fa-circle-check' : 'fa-triangle-exclamation'}"></i>
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
