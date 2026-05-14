// ─────────────────────────────────────────────
// admin.js — весь CRUD через Fetch API
// Страница НЕ перезагружается ни при каких действиях
// ─────────────────────────────────────────────

const API = '/admin';

// ── CSRF (нужен для POST / PUT / DELETE) ──────
function getCsrfHeaders() {
    return {
        'Content-Type': 'application/json'
    };
}

// ── Убрать ROLE_ префикс ──────────────────────
function formatRoles(roles) {
    return roles.map(r => r.name.replace('ROLE_', '')).join(', ');
}

// ═════════════════════════════════════════════
// 1. ЗАГРУЗКА ТЕКУЩЕГО ПОЛЬЗОВАТЕЛЯ (navbar)
// ═════════════════════════════════════════════
async function loadCurrentUser() {
    const res = await fetch('/user/current');
    const user = await res.json();

    document.getElementById('currentUserName').textContent  = user.username;
    document.getElementById('currentUserRoles').textContent = formatRoles(user.roles);
}

// ═════════════════════════════════════════════
// 2. ЗАГРУЗКА И РЕНДЕР ТАБЛИЦЫ ПОЛЬЗОВАТЕЛЕЙ
// ═════════════════════════════════════════════
async function loadUsers() {
    const res   = await fetch(`${API}/users`);
    const users = await res.json();
    renderTable(users);
}

function renderTable(users) {
    const tbody = document.getElementById('usersTableBody');
    tbody.innerHTML = '';

    users.forEach(user => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.lastName  || ''}</td>
            <td>${user.age       || ''}</td>
            <td>${user.email}</td>
            <td>${formatRoles(user.roles)}</td>
            <td>
                <button class="btn btn-info btn-sm text-white"
                        onclick="openEditModal(${user.id})">Edit</button>
            </td>
            <td>
                <button class="btn btn-danger btn-sm"
                        onclick="openDeleteModal(${user.id})">Delete</button>
            </td>`;
        tbody.appendChild(tr);
    });
}

// ═════════════════════════════════════════════
// 3. ЗАГРУЗКА РОЛЕЙ В <select>
// ═════════════════════════════════════════════
async function loadRolesIntoSelect(selectId, selectedRoles = []) {
    const res   = await fetch(`${API}/roles`);
    const roles = await res.json();

    const select      = document.getElementById(selectId);
    const selectedIds = selectedRoles.map(r => r.id);
    select.innerHTML  = '';

    roles.forEach(role => {
        const opt    = document.createElement('option');
        opt.value    = role.id;
        opt.textContent = role.name.replace('ROLE_', '');
        opt.selected = selectedIds.includes(role.id);
        select.appendChild(opt);
    });
}

// ═════════════════════════════════════════════
// 4. ДОБАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ
// ═════════════════════════════════════════════
document.getElementById('addUserForm').addEventListener('submit', async (e) => {
    e.preventDefault(); // страница НЕ перезагружается

    const roles = [...document.getElementById('addRoles').selectedOptions]
                    .map(o => ({ id: Number(o.value) }));

    const user = {
        username: document.getElementById('addUsername').value,
        lastName: document.getElementById('addLastName').value,
        age:      Number(document.getElementById('addAge').value),
        email:    document.getElementById('addEmail').value,
        password: document.getElementById('addPassword').value,
        roles
    };

    const res = await fetch(`${API}/users`, {
        method:  'POST',
        headers: getCsrfHeaders(),
        body:    JSON.stringify(user)
    });

    if (res.ok) {
        document.getElementById('addUserForm').reset();
        // Переключить на вкладку таблицы
        document.querySelector('[href="#usersTable"]').click();
        await loadUsers(); // обновить таблицу без перезагрузки
    } else {
        alert('Ошибка при создании пользователя');
    }
});

// ═════════════════════════════════════════════
// 5. РЕДАКТИРОВАНИЕ — открыть модалку
// ═════════════════════════════════════════════
async function openEditModal(id) {
    const res  = await fetch(`${API}/users/${id}`);
    const user = await res.json();

    document.getElementById('editId').value       = user.id;
    document.getElementById('editUsername').value = user.username;
    document.getElementById('editLastName').value = user.lastName  || '';
    document.getElementById('editAge').value      = user.age       || '';
    document.getElementById('editEmail').value    = user.email;
    document.getElementById('editPassword').value = '';

    await loadRolesIntoSelect('editRoles', user.roles);

    new bootstrap.Modal(document.getElementById('editModal')).show();
}

// ═════════════════════════════════════════════
// 5. РЕДАКТИРОВАНИЕ — отправить изменения
// ═════════════════════════════════════════════
async function submitEdit() {
    const id    = Number(document.getElementById('editId').value);
    const roles = [...document.getElementById('editRoles').selectedOptions]
                    .map(o => ({ id: Number(o.value) }));

    const user = {
        username: document.getElementById('editUsername').value,
        lastName: document.getElementById('editLastName').value,
        age:      Number(document.getElementById('editAge').value),
        email:    document.getElementById('editEmail').value,
        password: document.getElementById('editPassword').value,
        roles
    };

    const res = await fetch(`${API}/users/${id}`, {
        method:  'PUT',
        headers: getCsrfHeaders(),
        body:    JSON.stringify(user)
    });

    if (res.ok) {
        bootstrap.Modal.getInstance(document.getElementById('editModal')).hide();
        await loadUsers(); // обновить таблицу без перезагрузки
    } else {
        alert('Ошибка при обновлении пользователя');
    }
}

// ═════════════════════════════════════════════
// 6. УДАЛЕНИЕ — открыть модалку
// ═════════════════════════════════════════════
async function openDeleteModal(id) {
    const res  = await fetch(`${API}/users/${id}`);
    const user = await res.json();

    document.getElementById('deleteId').value       = user.id;
    document.getElementById('deleteUserId').value   = user.id;
    document.getElementById('deleteUsername').value = user.username;
    document.getElementById('deleteLastName').value = user.lastName || '';
    document.getElementById('deleteAge').value      = user.age      || '';
    document.getElementById('deleteEmail').value    = user.email;

    // Роли — бейджиками как было в оригинале
    const rolesDiv = document.getElementById('deleteRoles');
    rolesDiv.innerHTML = user.roles
        .map(r => `<span class="badge bg-secondary me-1">${r.name.replace('ROLE_', '')}</span>`)
        .join('');

    new bootstrap.Modal(document.getElementById('deleteModal')).show();
}

// ═════════════════════════════════════════════
// 6. УДАЛЕНИЕ — подтвердить
// ═════════════════════════════════════════════
async function submitDelete() {
    const id = document.getElementById('deleteId').value;

    const res = await fetch(`${API}/users/${id}`, {
        method:  'DELETE',
        headers: getCsrfHeaders()
    });

    if (res.ok) {
        bootstrap.Modal.getInstance(document.getElementById('deleteModal')).hide();
        await loadUsers(); // обновить таблицу без перезагрузки
    } else {
        alert('Ошибка при удалении пользователя');
    }
}

// ═════════════════════════════════════════════
// ИНИЦИАЛИЗАЦИЯ при загрузке страницы
// ═════════════════════════════════════════════
(async () => {
    await loadCurrentUser();              // navbar
    await loadRolesIntoSelect('addRoles'); // роли в форме добавления
    await loadUsers();                     // таблица
})();
