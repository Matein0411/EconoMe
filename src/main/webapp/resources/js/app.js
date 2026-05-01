document.addEventListener('DOMContentLoaded', () => {

    const userMenuButton = document.querySelector('.user-menu .user-avatar');
    const userDropdownMenu = document.querySelector('.user-menu .dropdown-menu');

    if (userMenuButton && userDropdownMenu) {
        userMenuButton.addEventListener('click', (event) => {
            event.stopPropagation();
            if (notificationList && notificationList.classList.contains('active')) {
                notificationList.classList.remove('active');
            }
            userDropdownMenu.classList.toggle('active');
        });
    }

    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('overlay');
    const openBtn = document.getElementById('sidebar-toggle-btn');
    const closeBtn = document.getElementById('close-sidebar-btn');

    if (sidebar && overlay && openBtn && closeBtn) {
        const openSidebar = () => {
            sidebar.classList.add('active');
            overlay.classList.add('active');
        };

        const closeSidebar = () => {
            sidebar.classList.remove('active');
            overlay.classList.remove('active');
        };

        openBtn.addEventListener('click', openSidebar);
        closeBtn.addEventListener('click', closeSidebar);
        overlay.addEventListener('click', closeSidebar);
    }

    const notificationBtn = document.querySelector('.notification-btn');
    const notificationList = document.getElementById('notification-list');
    const notificationCountBadge = document.getElementById('notification-count');

    if (notificationBtn && notificationList && notificationCountBadge) {

        async function fetchNotifications() {
            try {
                const response = await fetch(`${CONTEXT_PATH}/notificaciones`);
                if (!response.ok) {
                    throw new Error('La respuesta del servidor no fue exitosa.');
                }
                const notifications = await response.json();
                updateNotificationUI(notifications);
            } catch (error) {
                console.error('Falló la carga de notificaciones:', error);
                notificationList.innerHTML = '<div class="dropdown-item">Error al cargar.</div>';
            }
        }
        function updateNotificationUI(notifications) {
            notificationList.innerHTML = '';
            if (notifications.length === 0) {
                notificationCountBadge.style.display = 'none';
                notificationList.innerHTML = '<div class="dropdown-item no-notifications">No hay notificaciones nuevas.</div>';
            } else {
                notificationCountBadge.textContent = notifications.length;
                notificationCountBadge.style.display = 'block';
                notifications.forEach(notif => {
                    const item = document.createElement('a');
                    item.href = `${CONTEXT_PATH}/recordatorios`;
                    item.classList.add('dropdown-item');
                    item.innerHTML = `<strong>${notif.descripcion}</strong><br><small>Vence el: ${notif.fecha}</small>`;
                    notificationList.appendChild(item);
                });
            }
        }

        notificationBtn.addEventListener('click', (event) => {
            event.stopPropagation();
            if (userDropdownMenu && userDropdownMenu.classList.contains('active')) {
                userDropdownMenu.classList.remove('active');
            }
            notificationList.classList.toggle('active');

            if (notificationList.classList.contains('active')) {
                notificationCountBadge.style.display = 'none';
            }
        });

        fetchNotifications();
    }

    document.addEventListener('click', () => {
        if (userDropdownMenu && userDropdownMenu.classList.contains('active')) {
            userDropdownMenu.classList.remove('active');
        }

        if (notificationList && notificationList.classList.contains('active')) {
            notificationList.classList.remove('active');
        }
    });

});