// webapp/js/modal-confirm.js

const ModalConfirm = {
  modal: null,
  currentForm: null,

  init() {
    if (!this.modal) {
      this.createModal();
    }
  },

  createModal() {
    this.modal = document.createElement('div');
    this.modal.className = 'modal-overlay';
    this.modal.innerHTML = `
            <div class="modal-content">
                <div class="modal-header">
                    <div class="modal-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                            <line x1="12" y1="9" x2="12" y2="13"></line>
                            <line x1="12" y1="17" x2="12.01" y2="17"></line>
                        </svg>
                    </div>
                    <h3 class="modal-title" id="modal-title">¿Estás seguro?</h3>
                </div>
                <div class="modal-body">
                    <p class="modal-message" id="modal-message">Esta acción no se puede deshacer.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-cancel" id="modal-cancel">Cancelar</button>
                    <button type="button" class="btn btn-confirm" id="modal-confirm">Eliminar</button>
                </div>
            </div>
        `;

    document.body.appendChild(this.modal);

    // Event listeners
    this.modal.querySelector('#modal-cancel').addEventListener('click', () => this.hide());
    this.modal.querySelector('#modal-confirm').addEventListener('click', () => this.confirm());

    // Cerrar al hacer clic fuera del modal
    this.modal.addEventListener('click', (e) => {
      if (e.target === this.modal) {
        this.hide();
      }
    });

    // Cerrar con ESC
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && this.modal.classList.contains('active')) {
        this.hide();
      }
    });
  },

  show(options = {}) {
    this.init();

    const {
      title = '¿Estás seguro?',
      message = 'Esta acción no se puede deshacer.',
      confirmText = 'Eliminar',
      cancelText = 'Cancelar',
      onConfirm = null,
      form = null
    } = options;

    // Actualizar contenido
    this.modal.querySelector('#modal-title').textContent = title;
    this.modal.querySelector('#modal-message').textContent = message;
    this.modal.querySelector('#modal-confirm').textContent = confirmText;
    this.modal.querySelector('#modal-cancel').textContent = cancelText;

    // Guardar referencias
    this.currentForm = form;
    this.onConfirmCallback = onConfirm;

    // Mostrar modal
    this.modal.classList.add('active');
    document.body.style.overflow = 'hidden';
  },

  hide() {
    this.modal.classList.remove('active');
    document.body.style.overflow = '';
    this.currentForm = null;
    this.onConfirmCallback = null;
  },

  confirm() {
    if (this.onConfirmCallback) {
      this.onConfirmCallback();
    } else if (this.currentForm) {
      this.currentForm.submit();
    }
    this.hide();
  }
};

// Hacer disponible globalmente
window.ModalConfirm = ModalConfirm;