// ============================
//  Smart Notes — Frontend App
// ============================

const API = 'https://smart-notes-1-aqab.onrender.com/api/notes';

// =====================
//  Authentication
// =====================

function checkAuth() {
    const token = localStorage.getItem('token');
    const user = localStorage.getItem('user');
    
    if (!token || !user) {
        window.location.href = 'login.html';
        return;
    }
    
    try {
        const userData = JSON.parse(user);
        document.getElementById('userDisplay').textContent = `👤 ${userData.username}`;
        document.getElementById('logoutBtn').style.display = 'block';
        document.getElementById('logoutBtn').addEventListener('click', handleLogout);
    } catch (e) {
        window.location.href = 'login.html';
    }
}

function handleLogout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'login.html';
}

function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

const COLOR_BG_LIGHT = { default:'#ffffff', red:'#fff5f5', yellow:'#fefce8', green:'#f0fff4', blue:'#f0f9ff', purple:'#faf5ff', orange:'#fff8f0' };
const COLOR_BG_DARK  = { default:'#1e293b', red:'#2d1b1b', yellow:'#2d2a1b', green:'#1b2d22', blue:'#1b222d', purple:'#261b2d', orange:'#2d221b' };

// --- State ---
let allNotes       = [];
let activeCategory = 'all';
let activeSort     = 'newest';
let deleteTargetId = null;
let viewTargetId   = null;
let selectedColor  = 'default';
let isDarkMode     = false;

// --- DOM ---
const noteGrid        = document.getElementById('noteGrid');
const noteCount       = document.getElementById('noteCount');
const sectionTitle    = document.getElementById('sectionTitle');
const emptyMsg        = document.getElementById('emptyMsg');
const searchInput     = document.getElementById('searchInput');
const searchBtn       = document.getElementById('searchBtn');
const clearSearchBtn  = document.getElementById('clearSearchBtn');
const addNoteBtn      = document.getElementById('addNoteBtn');
const sortSelect      = document.getElementById('sortSelect');
const exportBtn       = document.getElementById('exportBtn');
const darkModeBtn     = document.getElementById('darkModeBtn');
const trashNavBtn     = document.getElementById('trashNavBtn');
const trashCount      = document.getElementById('trashCount');

// Modal – Add/Edit
const noteModal    = document.getElementById('noteModal');
const modalTitle   = document.getElementById('modalTitle');
const noteId       = document.getElementById('noteId');
const noteTitle    = document.getElementById('noteTitle');
const noteContent  = document.getElementById('noteContent');
const noteCategory = document.getElementById('noteCategory');
const titleError   = document.getElementById('titleError');
const saveBtn      = document.getElementById('saveBtn');
const cancelBtn    = document.getElementById('cancelBtn');
const closeModal   = document.getElementById('closeModal');
const wordCount    = document.getElementById('wordCount');
const colorPicker  = document.getElementById('colorPicker');

// Modal – View
const viewModal       = document.getElementById('viewModal');
const viewTitle       = document.getElementById('viewTitle');
const viewCategory    = document.getElementById('viewCategory');
const viewDate        = document.getElementById('viewDate');
const viewContent     = document.getElementById('viewContent');
const viewEditBtn     = document.getElementById('viewEditBtn');
const closeViewModal  = document.getElementById('closeViewModal');
const viewModalHeader = document.getElementById('viewModalHeader');

// Modal – Trash
const trashModal       = document.getElementById('trashModal');
const trashList        = document.getElementById('trashList');
const trashEmpty       = document.getElementById('trashEmpty');
const closeTrashModal  = document.getElementById('closeTrashModal');

// Modal – Delete
const deleteModal      = document.getElementById('deleteModal');
const deleteNoteName   = document.getElementById('deleteNoteName');
const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
const cancelDeleteBtn  = document.getElementById('cancelDeleteBtn');
const closeDeleteModal = document.getElementById('closeDeleteModal');

// Stats
const statTotal     = document.getElementById('statTotal');
const statPinned    = document.getElementById('statPinned');
const statTrashed   = document.getElementById('statTrashed');
const statCategories= document.getElementById('statCategories');

const toast = document.getElementById('toast');

// ======================
//  API
// ======================

const req = (url, opts = {}) =>
    fetch(url, { headers: getAuthHeaders(), ...opts })
        .then(r => { 
            if (r.status === 401) {
                window.location.href = 'login.html';
                throw new Error('Unauthorized');
            }
            if (!r.ok) throw new Error(r.status); 
            return r.status === 204 ? null : r.json(); 
        });

const fetchAllNotes     = (sort='newest')         => req(`${API}?sort=${sort}`);
const createNote        = data                    => req(API, { method:'POST', body: JSON.stringify(data) });
const updateNote        = (id, data)              => req(`${API}/${id}`, { method:'PUT', body: JSON.stringify(data) });
const softDelete        = id                      => req(`${API}/${id}`, { method:'DELETE' });
const permanentDelete   = id                      => req(`${API}/${id}/permanent`, { method:'DELETE' });
const restoreNote       = id                      => req(`${API}/${id}/restore`, { method:'PATCH' });
const togglePinApi      = id                      => req(`${API}/${id}/pin`, { method:'PATCH' });
const duplicateNoteApi  = id                      => req(`${API}/${id}/duplicate`, { method:'POST' });
const searchNotes       = kw                      => req(`${API}/search?keyword=${encodeURIComponent(kw)}`);
const fetchTrash        = ()                      => req(`${API}/trash`);
const fetchStats        = ()                      => req(`${API}/stats`);

// ======================
//  Render Notes
// ======================

function renderNotes(notes) {
    document.querySelectorAll('.note-card').forEach(c => c.remove());
    noteCount.textContent = notes.length;
    emptyMsg.style.display = notes.length === 0 ? 'block' : 'none';

    notes.forEach(note => {
        const card = document.createElement('div');
        card.className = 'note-card' + (note.pinned ? ' is-pinned' : '');
        card.dataset.id       = note.id;
        card.dataset.category = note.category || 'General';
        card.dataset.color    = note.color    || 'default';

        card.innerHTML = `
            ${note.pinned ? '<span class="pin-badge" title="Pinned">&#128204;</span>' : ''}
            <div class="note-card-top">
                <h3>${escHtml(note.title)}</h3>
                <div class="note-card-actions">
                    <button class="btn-icon pin ${note.pinned ? 'pinned-active' : ''}"
                            title="${note.pinned ? 'Unpin' : 'Pin'}" data-id="${note.id}">&#128204;</button>
                    <button class="btn-icon duplicate" title="Duplicate" data-id="${note.id}">&#10063;</button>
                    <button class="btn-icon edit-btn"  title="Edit"      data-id="${note.id}">&#9998;</button>
                    <button class="btn-icon delete delete-btn" title="Move to Trash"
                            data-id="${note.id}" data-title="${escHtml(note.title)}">&#128465;</button>
                </div>
            </div>
            <span class="note-card-category">${escHtml(note.category || 'General')}</span>
            <p class="note-card-content">${escHtml(note.content || '')}</p>
            <span class="note-card-date">Updated ${formatDate(note.updatedAt || note.createdAt)}</span>
        `;
        noteGrid.appendChild(card);

        card.addEventListener('click', e => {
            if (!e.target.closest('.note-card-actions')) openViewModal(note.id);
        });
    });

    document.querySelectorAll('.note-card .pin').forEach(btn =>
        btn.addEventListener('click', async e => { e.stopPropagation(); await handleTogglePin(+btn.dataset.id); })
    );
    document.querySelectorAll('.note-card .duplicate').forEach(btn =>
        btn.addEventListener('click', async e => { e.stopPropagation(); await handleDuplicate(+btn.dataset.id); })
    );
    document.querySelectorAll('.note-card .edit-btn').forEach(btn =>
        btn.addEventListener('click', e => { e.stopPropagation(); openEditModal(+btn.dataset.id); })
    );
    document.querySelectorAll('.note-card .delete-btn').forEach(btn =>
        btn.addEventListener('click', e => { e.stopPropagation(); openDeleteModal(+btn.dataset.id, btn.dataset.title); })
    );
}

async function loadNotes() {
    try {
        allNotes = await fetchAllNotes(activeSort);
        applyFilter();
        loadStats();
    } catch {
        showToast('Could not connect to server. Is the backend running?', 'error');
    }
}

function applyFilter() {
    sectionTitle.textContent = activeCategory === 'all' ? 'All Notes' : activeCategory;
    const filtered = activeCategory === 'all'
        ? allNotes
        : allNotes.filter(n => (n.category || 'General') === activeCategory);
    renderNotes(filtered);
}

// ======================
//  Stats
// ======================

async function loadStats() {
    try {
        const s = await fetchStats();
        statTotal.textContent   = s.total;
        statPinned.textContent  = s.pinned;
        statTrashed.textContent = s.trashed;

        // Trash badge on sidebar
        if (s.trashed > 0) {
            trashCount.textContent = s.trashed;
            trashCount.style.display = 'inline';
        } else {
            trashCount.style.display = 'none';
        }

        // Category breakdown
        statCategories.innerHTML = Object.entries(s.byCategory || {})
            .sort((a,b) => b[1] - a[1])
            .map(([cat, cnt]) => `
                <div class="stat-cat-row">
                    <span>${escHtml(cat)}</span>
                    <span>${cnt}</span>
                </div>
            `).join('');
    } catch { /* stats non-critical */ }
}

// ======================
//  Pin
// ======================

async function handleTogglePin(id) {
    try {
        const updated = await togglePinApi(id);
        allNotes = allNotes.map(n => n.id === updated.id ? updated : n);
        allNotes.sort((a, b) => (b.pinned ? 1 : 0) - (a.pinned ? 1 : 0));
        showToast(updated.pinned ? 'Note pinned!' : 'Note unpinned.', 'success');
        applyFilter();
        loadStats();
    } catch { showToast('Failed to toggle pin.', 'error'); }
}

// ======================
//  Duplicate
// ======================

async function handleDuplicate(id) {
    try {
        const copy = await duplicateNoteApi(id);
        allNotes.unshift(copy);
        showToast('Note duplicated!', 'success');
        applyFilter();
        loadStats();
    } catch { showToast('Failed to duplicate note.', 'error'); }
}

// ======================
//  Export
// ======================

exportBtn.addEventListener('click', async () => {
    try {
        const notes = await fetchAllNotes(activeSort);
        const blob = new Blob([JSON.stringify(notes, null, 2)], { type: 'application/json' });
        const url  = URL.createObjectURL(blob);
        const a    = document.createElement('a');
        a.href     = url;
        a.download = `smart-notes-${new Date().toISOString().slice(0,10)}.json`;
        a.click();
        URL.revokeObjectURL(url);
        showToast(`Exported ${notes.length} notes!`, 'success');
    } catch { showToast('Export failed.', 'error'); }
});

// ======================
//  Dark Mode
// ======================

function applyTheme(dark) {
    isDarkMode = dark;
    document.documentElement.setAttribute('data-theme', dark ? 'dark' : 'light');
    darkModeBtn.textContent = dark ? '☀' : '☾';
    localStorage.setItem('smartnotes-theme', dark ? 'dark' : 'light');
}

darkModeBtn.addEventListener('click', () => applyTheme(!isDarkMode));

// Restore saved theme
const savedTheme = localStorage.getItem('smartnotes-theme');
applyTheme(savedTheme === 'dark');

// ======================
//  Color Picker
// ======================

colorPicker.querySelectorAll('.color-swatch').forEach(s => {
    s.addEventListener('click', () => {
        colorPicker.querySelectorAll('.color-swatch').forEach(x => x.classList.remove('active'));
        s.classList.add('active');
        selectedColor = s.dataset.color;
    });
});

function setColorPicker(color) {
    selectedColor = color || 'default';
    colorPicker.querySelectorAll('.color-swatch').forEach(s =>
        s.classList.toggle('active', s.dataset.color === selectedColor));
}

// ======================
//  Word Count
// ======================

noteContent.addEventListener('input', () => {
    const w = noteContent.value.trim().split(/\s+/).filter(Boolean).length;
    wordCount.textContent = `${w} word${w !== 1 ? 's' : ''}`;
});

// ======================
//  Modal: Add / Edit
// ======================

function openAddModal() {
    modalTitle.textContent = 'New Note';
    noteId.value = '';
    noteTitle.value = noteContent.value = '';
    noteCategory.value = 'General';
    wordCount.textContent = '0 words';
    titleError.textContent = '';
    setColorPicker('default');
    noteModal.style.display = 'flex';
    setTimeout(() => noteTitle.focus(), 50);
}

function openEditModal(id) {
    const note = allNotes.find(n => n.id === id);
    if (!note) return;
    modalTitle.textContent    = 'Edit Note';
    noteId.value              = note.id;
    noteTitle.value           = note.title;
    noteContent.value         = note.content || '';
    noteCategory.value        = note.category || 'General';
    titleError.textContent    = '';
    setColorPicker(note.color || 'default');
    const w = (note.content || '').trim().split(/\s+/).filter(Boolean).length;
    wordCount.textContent = `${w} word${w !== 1 ? 's' : ''}`;
    noteModal.style.display = 'flex';
    setTimeout(() => noteTitle.focus(), 50);
}

const closeNoteModal = () => { noteModal.style.display = 'none'; };

saveBtn.addEventListener('click', async () => {
    const title = noteTitle.value.trim();
    if (!title) { titleError.textContent = 'Title is required.'; return; }
    titleError.textContent = '';

    const payload = { title, content: noteContent.value.trim(), category: noteCategory.value, color: selectedColor };
    try {
        const id = noteId.value;
        if (id) {
            const updated = await updateNote(+id, payload);
            allNotes = allNotes.map(n => n.id === updated.id ? updated : n);
            showToast('Note updated!', 'success');
        } else {
            const created = await createNote(payload);
            allNotes.unshift(created);
            showToast('Note created!', 'success');
        }
        closeNoteModal();
        applyFilter();
        loadStats();
    } catch { showToast('Error saving note.', 'error'); }
});

addNoteBtn.addEventListener('click', openAddModal);
cancelBtn.addEventListener('click', closeNoteModal);
closeModal.addEventListener('click', closeNoteModal);
noteModal.addEventListener('click', e => { if (e.target === noteModal) closeNoteModal(); });

// ======================
//  Modal: View
// ======================

function openViewModal(id) {
    const note = allNotes.find(n => n.id === id);
    if (!note) return;
    viewTargetId = id;

    viewTitle.textContent   = note.title;
    viewCategory.textContent= note.category || 'General';
    viewDate.textContent    = formatDate(note.updatedAt || note.createdAt);
    viewContent.textContent = note.content || '(no content)';

    const colorMap = isDarkMode ? COLOR_BG_DARK : COLOR_BG_LIGHT;
    viewModalHeader.style.background = colorMap[note.color] || colorMap.default;

    viewModal.style.display = 'flex';
}

const closeView = () => { viewModal.style.display = 'none'; };
viewEditBtn.addEventListener('click', () => { closeView(); openEditModal(viewTargetId); });
closeViewModal.addEventListener('click', closeView);
viewModal.addEventListener('click', e => { if (e.target === viewModal) closeView(); });

// ======================
//  Modal: Trash
// ======================

async function openTrashModal() {
    trashList.innerHTML = '';
    trashEmpty.style.display = 'none';
    trashModal.style.display = 'flex';
    try {
        const trashed = await fetchTrash();
        if (trashed.length === 0) { trashEmpty.style.display = 'block'; return; }
        trashed.forEach(note => {
            const item = document.createElement('div');
            item.className = 'trash-item';
            item.innerHTML = `
                <div class="trash-item-info">
                    <div class="trash-item-title">${escHtml(note.title)}</div>
                    <div class="trash-item-meta">${escHtml(note.category||'General')} · Deleted ${formatDate(note.updatedAt)}</div>
                </div>
                <div class="trash-item-actions">
                    <button class="btn btn-ghost restore-btn" data-id="${note.id}">Restore</button>
                    <button class="btn btn-danger perm-btn" data-id="${note.id}">Delete</button>
                </div>
            `;
            trashList.appendChild(item);
        });

        trashList.querySelectorAll('.restore-btn').forEach(btn =>
            btn.addEventListener('click', async () => {
                try {
                    const restored = await restoreNote(+btn.dataset.id);
                    allNotes.unshift(restored);
                    showToast('Note restored!', 'success');
                    openTrashModal();
                    applyFilter();
                    loadStats();
                } catch { showToast('Restore failed.', 'error'); }
            })
        );

        trashList.querySelectorAll('.perm-btn').forEach(btn =>
            btn.addEventListener('click', async () => {
                if (!confirm('Permanently delete this note? This cannot be undone.')) return;
                try {
                    await permanentDelete(+btn.dataset.id);
                    showToast('Permanently deleted.', 'success');
                    openTrashModal();
                    loadStats();
                } catch { showToast('Delete failed.', 'error'); }
            })
        );
    } catch { showToast('Could not load trash.', 'error'); }
}

trashNavBtn.addEventListener('click', openTrashModal);
closeTrashModal.addEventListener('click', () => { trashModal.style.display = 'none'; });
trashModal.addEventListener('click', e => { if (e.target === trashModal) trashModal.style.display = 'none'; });

// ======================
//  Modal: Delete
// ======================

function openDeleteModal(id, title) {
    deleteTargetId = id;
    deleteNoteName.textContent = `"${title}"`;
    deleteModal.style.display = 'flex';
}

const closeDelete = () => { deleteModal.style.display = 'none'; deleteTargetId = null; };

confirmDeleteBtn.addEventListener('click', async () => {
    if (!deleteTargetId) return;
    try {
        await softDelete(deleteTargetId);
        allNotes = allNotes.filter(n => n.id !== deleteTargetId);
        showToast('Moved to trash.', 'success');
        closeDelete();
        applyFilter();
        loadStats();
    } catch { showToast('Error deleting note.', 'error'); }
});
cancelDeleteBtn.addEventListener('click', closeDelete);
closeDeleteModal.addEventListener('click', closeDelete);
deleteModal.addEventListener('click', e => { if (e.target === deleteModal) closeDelete(); });

// ======================
//  Search
// ======================

searchBtn.addEventListener('click', async () => {
    const kw = searchInput.value.trim();
    if (!kw) return;
    try {
        const results = await searchNotes(kw);
        sectionTitle.textContent = `Search: "${kw}"`;
        renderNotes(results);
        clearSearchBtn.style.display = 'inline-block';
    } catch { showToast('Search failed.', 'error'); }
});
searchInput.addEventListener('keydown', e => { if (e.key === 'Enter') searchBtn.click(); });
clearSearchBtn.addEventListener('click', () => {
    searchInput.value = '';
    clearSearchBtn.style.display = 'none';
    applyFilter();
});

// ======================
//  Sort
// ======================

sortSelect.addEventListener('change', async () => {
    activeSort = sortSelect.value;
    try {
        allNotes = await fetchAllNotes(activeSort);
        applyFilter();
    } catch { showToast('Sort failed.', 'error'); }
});

// ======================
//  Category
// ======================

document.querySelectorAll('#categoryList li').forEach(item => {
    item.addEventListener('click', () => {
        document.querySelectorAll('#categoryList li').forEach(i => i.classList.remove('active'));
        item.classList.add('active');
        activeCategory = item.dataset.category;
        searchInput.value = '';
        clearSearchBtn.style.display = 'none';
        applyFilter();
    });
});

// ======================
//  Keyboard Shortcuts
// ======================

document.addEventListener('keydown', e => {
    const tag = document.activeElement.tagName;
    const inInput = ['INPUT','TEXTAREA','SELECT'].includes(tag);

    if (e.key === 'Escape') {
        [noteModal, viewModal, trashModal, deleteModal].forEach(m => { m.style.display = 'none'; });
    }
    if (e.ctrlKey && e.key === 'n' && !inInput) {
        e.preventDefault();
        openAddModal();
    }
    if (e.ctrlKey && e.key === 'f' && !inInput) {
        e.preventDefault();
        searchInput.focus();
    }
});

// ======================
//  Helpers
// ======================

function escHtml(str) {
    const d = document.createElement('div');
    d.textContent = str;
    return d.innerHTML;
}

function formatDate(iso) {
    if (!iso) return '';
    return new Date(iso).toLocaleDateString('en-US', { month:'short', day:'numeric', year:'numeric' });
}

let toastTimer;
function showToast(msg, type = '') {
    toast.textContent = msg;
    toast.className = `toast ${type}`;
    toast.style.display = 'block';
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => { toast.style.display = 'none'; }, 3000);
}

// ======================
//  Init
// ======================

// Check authentication first
checkAuth();

loadNotes();
