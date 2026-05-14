// ─────────────────────────────────────────────
// user.js — загрузка данных текущего пользователя
// через GET /api/user без перезагрузки страницы
// ─────────────────────────────────────────────

async function loadCurrentUser() {
    const res = await fetch('/user/current');

    if (!res.ok) {
        window.location.href = '/login';
        return;
    }

    const user = await res.json();

    document.getElementById('userId').textContent        = user.id;
    document.getElementById('userUsername').textContent  = user.username;
    document.getElementById('userLastName').textContent  = user.lastName  || '—';
    document.getElementById('userAge').textContent       = user.age       || '—';
    document.getElementById('userEmail').textContent     = user.email;
    document.getElementById('userRoles').textContent     =
        user.roles.map(r => r.name.replace('ROLE_', '')).join(', ');

    // Navbar
    document.getElementById('currentUserName').textContent  = user.username;
    document.getElementById('currentUserRoles').textContent =
        user.roles.map(r => r.name.replace('ROLE_', '')).join(', ');
}

loadCurrentUser();
