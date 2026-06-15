/* ============================================================
   EventSphere — Shared API, Authentication & Engine Helpers
============================================================ */

const API = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
  ? 'http://localhost:8085/api'
  : 'https://eventsphere-e553.onrender.com/api';

/* ── HTTP helper ── */
async function http(method, path, body) {
  const token = localStorage.getItem('token')
  const opts  = {
    method,
    headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}) },
    ...(body ? { body: JSON.stringify(body) } : {})
  }
  const res  = await fetch(API + path, opts)
  if (res.status === 401) { doLogout(); return null }
  const data = await res.json().catch(() => ({}))
  if (!res.ok) throw new Error(data.message || 'Request failed')
  return data
}
const GET    = (p)    => http('GET',    p)
const POST   = (p, b) => http('POST',   p, b)
const PATCH  = (p)    => http('PATCH',  p)
const DELETE = (p)    => http('DELETE', p)

/* ── Auth helpers ── */
function getUser() {
  const s = localStorage.getItem('user')
  return s ? JSON.parse(s) : null
}

function doLogout() {
  localStorage.clear()
  window.location.href = '/'; // FIXED: Route cleanly back to root routing index instead of static file
}

function requireAuth() {
  if (!getUser()) { window.location.href = 'login'; return false } // FIXED: Changed 'login.html' to clean 'login' route
  return true
}

/* ── Navbar builder with integrated profile dropdown logic ── */
function buildNavbar(activePage) {
  const user = getUser()
  const nav = document.createElement('nav')
  nav.className = 'navbar'
  nav.innerHTML = `
    <a class="nav-logo" href="/">Event<span>Sphere</span></a>
    <div class="nav-links">
      <a class="nav-btn${activePage==='home'?' active':''}" href="/">Home</a>
      <a class="nav-btn${activePage==='events'?' active':''}" href="events.html">Events</a>
      ${user ? `
        <div class="user-menu-wrapper" style="position: relative; display: inline-block;">
          <div class="user-pill" id="profilePill" style="cursor:pointer">
            <div class="avatar">${user.name?.charAt(0).toUpperCase()||'U'}</div>
            <span style="font-size:.85rem;font-weight:500">${user.name || 'User'}</span>
            <span class="role-badge role-${user.role || 'STUDENT'}">${user.role || 'STUDENT'}</span>
          </div>
          <div class="dropdown-menu" id="dropdownMenu">
            <a class="dropdown-item" href="dashboard.html">Dashboard</a>
            <button class="dropdown-item logout-btn" id="logoutAction">Log Out</button>
          </div>
        </div>
      ` : `
        <a class="btn-outline" href="login" style="margin-left:8px">Log in</a>
        <a class="btn-primary" href="register">Sign up</a>
      `}
    </div>`
  document.body.prepend(nav)

  // Attach interactive click listeners securely after creation
  if (user) {
    const profilePill = document.getElementById('profilePill');
    const dropdownMenu = document.getElementById('dropdownMenu');
    const logoutAction = document.getElementById('logoutAction');

    if (profilePill && dropdownMenu) {
      profilePill.addEventListener('click', function (e) {
        e.stopPropagation();
        dropdownMenu.classList.toggle('show');
      });
      document.addEventListener('click', function () {
        dropdownMenu.classList.remove('show');
      });
    }

    if (logoutAction) {
      logoutAction.addEventListener('click', function () {
        doLogout();
      });
    }
  }
}

/* ── System Toast Notifications ── */
function ensureToastBox() {
  if (!document.getElementById('toastBox')) {
    const b = document.createElement('div')
    b.id = 'toastBox'
    document.body.appendChild(b)
  }
}
function toast(msg, type = 'ok') {
  ensureToastBox()
  const box = document.getElementById('toastBox')
  const t   = document.createElement('div')
  t.className = `toast ${type === 'ok' ? 'toast-success' : 'toast-error'}`
  t.innerHTML = msg
  box.appendChild(t)
  setTimeout(() => t.remove(), 3500)
}

/* ── Shared formatters ── */
function fmtDate(d) {
  if (!d) return 'TBA'
  return new Date(d).toLocaleDateString('en-IN', { day:'numeric', month:'short', year:'numeric' })
}
function fmtTime(d) {
  if (!d) return ''
  return new Date(d).toLocaleTimeString('en-IN', { hour:'2-digit', minute:'2-digit' })
}
function catEmoji(c) {
  return { HACKATHON:'💻', WORKSHOP:'🛠️', SEMINAR:'🎤', CULTURAL:'🎭' }[c] || '🎪'
}
function seatsColor(pct) {
  if (pct >= 90) return '#f25f5c'
  if (pct >= 70) return '#f5a623'
  return '#3ecf8e'
}
function eventCard(e) {
  const pct  = e.totalSeats > 0 ? Math.round(e.registeredCount / e.totalSeats * 100) : 0
  const full = e.registeredCount >= e.totalSeats
  return `
  <div class="card" onclick="window.location.href='event-detail.html?id=${e.id}'" style="cursor:pointer">
    <div class="card-img">
      ${catEmoji(e.category)}
      <div style="position:absolute;top:10px;left:10px"><span class="cat-badge cat-${e.category}">${e.category||'EVENT'}</span></div>
      ${e.leaderboardEnabled ? '<div style="position:absolute;top:10px;right:10px;font-size:.7rem;background:rgba(245,166,35,.2);color:var(--amber);border:1px solid rgba(245,166,35,.4);padding:3px 8px;border-radius:999px;font-weight:700">LIVE LB</div>' : ''}
    </div>
    <div class="card-body">
      <div class="card-title">${e.title}</div>
      <div class="card-meta">
        <span class="meta-item">📅 ${fmtDate(e.startDateTime)}</span>
        <span class="meta-item">⏰ ${fmtTime(e.startDateTime)}</span>
        <span class="meta-item">📍 ${e.venue || 'TBA'}</span>
        ${e.teamEvent ? `<span class="meta-item">👥 Team (max ${e.maxTeamSize})</span>` : ''}
      </div>
    </div>
    <div class="card-footer">
      <span style="font-size:.72rem;color:var(--muted)">${e.registeredCount}/${e.totalSeats} seats</span>
      <div class="seats-bar"><div class="seats-fill" style="width:${pct}%;background:${seatsColor(pct)}"></div></div>
      <span style="font-size:.72rem;font-weight:600;color:${full ? 'var(--red)' : 'var(--green)'}">${full ? 'FULL' : e.totalSeats - e.registeredCount + ' left'}</span>
    </div>
  </div>`
}