/**
 * Gestor de Notificaciones
 */
class NotificacionesManager {
    constructor() {
        this.notificationBtn = document.querySelector('.notification-btn');
        this.notificationList = document.getElementById('notification-list');
        this.notificationCount = document.getElementById('notification-count');
        this.notificaciones = [];

        this.init();
    }

    init() {
        if (!this.notificationBtn || !this.notificationList) {
            console.warn('Elementos de notificación no encontrados');
            return;
        }

        this.cargarNotificaciones();

        this.notificationBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            this.toggleDropdown();
        });

        document.addEventListener('click', (e) => {
            if (!this.notificationList.contains(e.target)) {
                this.cerrarDropdown();
            }
        });

        setInterval(() => this.cargarNotificaciones(), 5 * 60 * 1000);
    }

    async cargarNotificaciones() {
        try {
            const response = await fetch(`${CONTEXT_PATH}/notificaciones`, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json'
                }
            });

            if (!response.ok) {
                if (response.status === 401) {
                    console.warn('Usuario no autenticado');
                    return;
                }
                throw new Error(`Error HTTP: ${response.status}`);
            }

            this.notificaciones = await response.json();
            this.renderizarNotificaciones();
            this.actualizarContador();

        } catch (error) {
            console.error('Error al cargar notificaciones:', error);
            this.mostrarError();
        }
    }

    renderizarNotificaciones() {
        if (this.notificaciones.length === 0) {
            this.notificationList.innerHTML = '<div class="no-notifications">No hay notificaciones pendientes</div>';
            return;
        }

        const notificacionesHTML = this.notificaciones.map(notif => `
            <div class="notification-item">
                <div class="notification-title">Recordatorio de Pago</div>
                <div class="notification-info">
                    <div class="notification-info-row">
                        <span class="notification-info-label">Descripción:</span>
                        <span class="notification-info-value">${this.escapeHtml(notif.descripcion)}</span>
                    </div>
                    <div class="notification-info-row">
                        <span class="notification-info-label">Monto:</span>
                        <span class="notification-info-value notification-amount">$${this.formatearMonto(notif.monto)}</span>
                    </div>
                    <div class="notification-info-row">
                        <span class="notification-info-label">Fecha de pago:</span>
                        <span class="notification-info-value">${this.formatearFecha(notif.fechaVencimiento)}</span>
                    </div>
                </div>
            </div>
        `).join('');

        this.notificationList.innerHTML = notificacionesHTML;
    }

    actualizarContador() {
        const count = this.notificaciones.length;

        if (count > 0) {
            this.notificationCount.textContent = count > 99 ? '99+' : count;
            this.notificationCount.style.display = 'flex';
        } else {
            this.notificationCount.style.display = 'none';
        }
    }

    toggleDropdown() {
        const isVisible = this.notificationList.classList.contains('show');

        if (isVisible) {
            this.cerrarDropdown();
        } else {
            this.abrirDropdown();
        }
    }

    abrirDropdown() {
        this.notificationList.classList.add('show');
        this.notificationBtn.classList.add('active');
    }

    cerrarDropdown() {
        this.notificationList.classList.remove('show');
        this.notificationBtn.classList.remove('active');
    }

    mostrarError() {
        this.notificationList.innerHTML = '<div class="no-notifications">Error al cargar notificaciones</div>';
    }

    formatearFecha(fechaStr) {
        const fecha = new Date(fechaStr + 'T00:00:00');
        return fecha.toLocaleDateString('es-EC', {
            day: '2-digit',
            month: 'long',
            year: 'numeric'
        });
    }

    formatearMonto(monto) {
        return parseFloat(monto).toFixed(2);
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new NotificacionesManager();
});