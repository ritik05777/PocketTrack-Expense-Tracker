const API_BASE = 'http://localhost:8080/api/expenses';
const $ = (id) => document.getElementById(id);
let expenses = [];
let editingId = null;

const money = (value) => new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(Number(value || 0));
const icons = { Food: '🍜', Transport: '🚗', Shopping: '🛍️', Bills: '🧾', Health: '💊', Entertainment: '🎬', Education: '📚', Other: '✦' };

function showMessage(message, isError = false) {
  $('formMessage').textContent = message;
  $('formMessage').classList.toggle('error', isError);
}

async function api(path = '', options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) }
  });
  if (response.status === 204) return null;
  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    const details = data.details ? Object.values(data.details).join(' · ') : data.message || data.error || 'Request failed';
    throw new Error(details);
  }
  return data;
}

async function loadExpenses() {
  try {
    const category = $('filterCategory').value;
    const query = category ? `?category=${encodeURIComponent(category)}` : '';
    expenses = await api(query);
    renderExpenses();
    await loadSummary();
  } catch (error) {
    $('expenseList').innerHTML = `<div class="empty"><b>Couldn't load expenses</b>${escapeHtml(error.message)}<br>Check that Spring Boot is running.</div>`;
  }
}

async function loadSummary() {
  try {
    const summary = await api('/summary');
    $('allTimeTotal').textContent = money(summary.allTimeTotal);
    $('monthTotal').textContent = money(summary.currentMonthTotal);
    $('expenseCount').textContent = expenses.length;
    $('listCount').textContent = `${expenses.length} record${expenses.length === 1 ? '' : 's'}`;
  } catch (error) { console.error(error); }
}

function escapeHtml(value = '') {
  return String(value).replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

function renderExpenses() {
  const term = $('search').value.trim().toLowerCase();
  const filtered = expenses.filter(e => `${e.title} ${e.category} ${e.notes || ''}`.toLowerCase().includes(term));
  $('listCount').textContent = `${filtered.length} record${filtered.length === 1 ? '' : 's'}`;
  if (!filtered.length) {
    $('expenseList').innerHTML = '<div class="empty"><b>No expenses found</b>Add an expense or adjust your search.</div>';
    return;
  }
  $('expenseList').innerHTML = filtered.map(e => `
    <article class="expense-row">
      <div class="expense-main"><div class="category-icon">${icons[e.category] || '✦'}</div><div style="min-width:0"><div class="expense-title">${escapeHtml(e.title)}</div><div class="expense-meta">${escapeHtml(e.category)} · ${escapeHtml(e.expenseDate)}${e.notes ? ' · ' + escapeHtml(e.notes) : ''}</div></div></div>
      <div class="expense-side"><div class="expense-amount">${money(e.amount)}</div><div class="row-actions"><button data-edit="${e.id}">Edit</button><button data-delete="${e.id}">Delete</button></div></div>
    </article>`).join('');
}

function resetForm() {
  editingId = null;
  $('expenseForm').reset();
  $('expenseDate').value = new Date().toISOString().slice(0, 10);
  $('formHeading').textContent = 'Add an expense';
  $('submitButton').innerHTML = 'Save expense <span>↗</span>';
  $('cancelEdit').classList.add('hidden');
}

$('expenseForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = {
    title: $('title').value.trim(),
    amount: Number($('amount').value),
    category: $('category').value,
    expenseDate: $('expenseDate').value,
    notes: $('notes').value.trim() || null
  };
  try {
    if (editingId === null) await api('', { method: 'POST', body: JSON.stringify(payload) });
    else await api(`/${editingId}`, { method: 'PUT', body: JSON.stringify(payload) });
    showMessage(editingId === null ? 'Expense saved successfully.' : 'Expense updated successfully.');
    resetForm();
    await loadExpenses();
  } catch (error) { showMessage(error.message, true); }
});

$('expenseList').addEventListener('click', async (event) => {
  const editButton = event.target.closest('[data-edit]');
  const deleteButton = event.target.closest('[data-delete]');
  if (editButton) {
    const expense = expenses.find(e => e.id === Number(editButton.dataset.edit));
    if (!expense) return;
    editingId = expense.id;
    $('title').value = expense.title;
    $('amount').value = expense.amount;
    $('category').value = expense.category;
    $('expenseDate').value = expense.expenseDate;
    $('notes').value = expense.notes || '';
    $('formHeading').textContent = `Edit expense #${expense.id}`;
    $('submitButton').innerHTML = 'Update expense <span>↗</span>';
    $('cancelEdit').classList.remove('hidden');
    $('title').focus();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
  if (deleteButton) {
    const id = Number(deleteButton.dataset.delete);
    if (!confirm('Delete this expense? This cannot be undone.')) return;
    try { await api(`/${id}`, { method: 'DELETE' }); await loadExpenses(); }
    catch (error) { alert(error.message); }
  }
});

$('cancelEdit').addEventListener('click', resetForm);
$('filterCategory').addEventListener('change', loadExpenses);
$('search').addEventListener('input', renderExpenses);
$('expenseDate').value = new Date().toISOString().slice(0, 10);
loadExpenses();
