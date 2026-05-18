const API = '/admin';

// Все fetch-запросы за данными явно запрашивают JSON,
// чтобы Spring выбирал produces=APPLICATION_JSON метод, а не HTML
function jsonHeaders() {
    return {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    };
}

function getHeaders() {
    return { 'Accept': 'application/json' };
}

function formatRoles(roles) {
    if (!roles || !Array.isArray(roles)) return '—';
    return roles.map(r => r.name ? r.name.replace('ROLE_', '') : r).join(', ');
}

// ====================== ЗАГРУЗКА ДАННЫХ ======================
async function loadCurrentUser() {
    try {
        const res = await fetch('/user/current', { headers: getHeaders() });
        if (!res.ok) throw new Error('Unauthorized');
        const user = await res.json();
        document.getElementById('currentUserName').textContent = user.username || user.email || 'User';
        document.getElementById('currentUserRoles').textContent = formatRoles(user.roles);
    } catch (err) {
        console.error('loadCurrentUser error:', err);
    }
}

async function loadUsers() {
    try {
        const res = await fetch(`${API}/users`, { headers: getHeaders() });
        if (!res.ok) throw new Error('Failed to load users: ' + res.status);
        const users = await res.json();
        renderTable(users);
    } catch (err) {
        console.error('loadUsers error:', err);
    }
}

function renderTable(users) {
    const tbody = document.getElementById('usersTableBody');
    if (!tbody) return;
    tbody.innerHTML = '';
    users.forEach(user => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${user.id}</td>
            <td>${user.username || ''}</td>
            <td>${user.lastName || ''}</td>
            <td>${user.age || ''}</td>
            <td>${user.email}</td>
            <td>${formatRoles(user.roles)}</td>
            <td><button class="btn btn-info btn-sm text-white" onclick="openEditModal(${user.id})">Edit</button></td>
            <td><button class="btn btn-danger btn-sm" onclick="openDeleteModal(${user.id})">Delete</button></td>
        `;
        tbody.appendChild(tr);
    });
}

// ====================== РОЛИ ======================
async function loadRolesIntoSelect(selectId, selectedRoles = []) {
    try {
        const res = await fetch(`${API}/roles`, { headers: getHeaders() });
        const roles = await res.json();
        const select = document.getElementById(selectId);
        if (!select) return;
        const selectedIds = selectedRoles.map(r => r.id);
        select.innerHTML = '';
        roles.forEach(role => {
            const opt = document.createElement('option');
            opt.value = role.id;
            opt.textContent = role.name.replace('ROLE_', '');
            opt.selected = selectedIds.includes(role.id);
            select.appendChild(opt);
        });
    } catch (err) {
        console.error('loadRolesIntoSelect error:', err);
    }
}

// ====================== ДОБАВЛЕНИЕ ======================
async function submitAdd(event) {
    event.preventDefault();
    const roles = [...document.getElementById('addRoles').selectedOptions]
        .map(o => ({ id: Number(o.value) }));
    const user = {
        username: document.getElementById('addUsername').value,
        lastName: document.getElementById('addLastName').value,
        age: Number(document.getElementById('addAge').value),
        email: document.getElementById('addEmail').value,
        password: document.getElementById('addPassword').value,
        roles
    };
    try {
        const res = await fetch(`${API}/users`, {
            method: 'POST',
            headers: jsonHeaders(),
            body: JSON.stringify(user)
        });
        if (res.ok) {
            document.getElementById('addUserForm').reset();
            bootstrap.Tab.getOrCreateInstance(
                document.querySelector('[href="#usersTable"]')
            ).show();
            loadUsers();
        } else {
            alert('Ошибка при создании пользователя');
        }
    } catch (err) {
        console.error('submitAdd error:', err);
    }
}

// ====================== РЕДАКТИРОВАНИЕ ======================
async function openEditModal(id) {
    try {
        const res = await fetch(`${API}/users/${id}`, { headers: getHeaders() });
        const user = await res.json();
        document.getElementById('editId').value = user.id;
        document.getElementById('editUsername').value = user.username || '';
        document.getElementById('editLastName').value = user.lastName || '';
        document.getElementById('editAge').value = user.age || '';
        document.getElementById('editEmail').value = user.email;
        document.getElementById('editPassword').value = '';
        await loadRolesIntoSelect('editRoles', user.roles);
        new bootstrap.Modal(document.getElementById('editModal')).show();
    } catch (err) {
        console.error('openEditModal error:', err);
        alert('Ошибка загрузки пользователя');
    }
}

async function submitEdit() {
    const id = Number(document.getElementById('editId').value);
    const roles = [...document.getElementById('editRoles').selectedOptions]
        .map(o => ({ id: Number(o.value) }));
    const user = {
        username: document.getElementById('editUsername').value,
        lastName: document.getElementById('editLastName').value,
        age: Number(document.getElementById('editAge').value),
        email: document.getElementById('editEmail').value,
        password: document.getElementById('editPassword').value,
        roles
    };
    try {
        const res = await fetch(`${API}/users/${id}`, {
            method: 'PUT',
            headers: jsonHeaders(),
            body: JSON.stringify(user)
        });
        if (res.ok) {
            bootstrap.Modal.getInstance(document.getElementById('editModal')).hide();
            loadUsers();
        } else {
            alert('Ошибка обновления');
        }
    } catch (err) {
        console.error('submitEdit error:', err);
    }
}

// ====================== УДАЛЕНИЕ ======================
async function openDeleteModal(id) {
    try {
        const res = await fetch(`${API}/users/${id}`, { headers: getHeaders() });
        const user = await res.json();
        document.getElementById('deleteId').value = user.id;
        document.getElementById('deleteUserId').value = user.id;
        document.getElementById('deleteUsername').value = user.username || '';
        document.getElementById('deleteLastName').value = user.lastName || '';
        document.getElementById('deleteAge').value = user.age || '';
        document.getElementById('deleteEmail').value = user.email;
        document.getElementById('deleteRoles').innerHTML = user.roles
            .map(r => `<span class="badge bg-secondary me-1">${r.name.replace('ROLE_', '')}</span>`)
            .join('');
        new bootstrap.Modal(document.getElementById('deleteModal')).show();
    } catch (err) {
        console.error('openDeleteModal error:', err);
    }
}

async function submitDelete() {
    const id = document.getElementById('deleteId').value;
    try {
        const res = await fetch(`${API}/users/${id}`, {
            method: 'DELETE',
            headers: jsonHeaders()
        });
        if (res.ok) {
            bootstrap.Modal.getInstance(document.getElementById('deleteModal')).hide();
            loadUsers();
        }
    } catch (err) {
        console.error('submitDelete error:', err);
    }
}

// ====================== ИНИЦИАЛИЗАЦИЯ ======================
document.addEventListener('DOMContentLoaded', () => {
    loadCurrentUser();
    loadUsers();
    if (document.getElementById('addRoles')) {
        loadRolesIntoSelect('addRoles');
    }
    const addForm = document.getElementById('addUserForm');
    if (addForm) {
        addForm.addEventListener('submit', submitAdd);
    }
});
