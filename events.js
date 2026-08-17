// js/events.js
// ================================================================
// Member 3: Event Discovery & Registration.
// The browser only ever talks to this server's own /api/events
// endpoints. "Who am I" is just a typed profile name (same pattern
// ProfileManager already uses for friend lookups) -- there's no
// login system in this app, so the name box above the event list
// doubles as identification for register/cancel/duplicate checks.
// ================================================================

const API_BASE = "/api/events";

let currentEventId = null;
let currentScope = "upcoming";


// ================================================================
// Helpers
// ================================================================

function setStatus(message, isError = false) {
  const bar    = document.getElementById("status-message");
  const footer = document.getElementById("status-bar");
  bar.textContent         = message;
  footer.style.background = isError ? "#6b1a1a" : "var(--clr-status-bg)";
  footer.style.color      = isError ? "#ffcccc"  : "var(--clr-status-text)";
}

function getMyName() {
  return document.getElementById("input-my-name").value.trim();
}

function formatWhen(startTime, endTime) {
  const start = new Date(startTime);
  const opts  = { dateStyle: "medium", timeStyle: "short" };
  let text = start.toLocaleString(undefined, opts);
  if (endTime) {
    const end = new Date(endTime);
    text += ` \u2013 ${end.toLocaleString(undefined, opts)}`;
  }
  return text;
}

function isPast(startTime) {
  return new Date(startTime).getTime() < Date.now();
}

// api -- small fetch wrapper, same shape as app.js's version.
async function api(path, options = {}) {
  const response = await fetch(API_BASE + path, options);

  const rawText = await response.text();
  let body = null;
  if (rawText) {
    try {
      body = JSON.parse(rawText);
    } catch {
      throw new Error(`Server returned HTTP ${response.status} (not JSON).`);
    }
  }

  if (!response.ok) {
    throw new Error((body && body.error) || `Server error ${response.status}.`);
  }

  return body;
}

function apiJson(path, method, payload) {
  return api(path, {
    method,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
}


// ================================================================
// Event list (Upcoming / Next 2 Weeks / All)
// ================================================================

async function loadEventList() {
  try {
    const events = await api(`?scope=${currentScope}`);

    const container = document.getElementById("event-list");
    container.innerHTML = "";

    if (events.length === 0) {
      container.innerHTML = '<p class="empty-state">No events found for this filter.</p>';
      return;
    }

    events.forEach((event) => {
      const row = document.createElement("div");
      row.className  = "event-item";
      row.dataset.id = event.id;
      if (event.id === currentEventId) row.classList.add("active");

      const title = document.createElement("div");
      title.className   = "event-item-title";
      title.textContent = event.title;

      const meta = document.createElement("div");
      meta.className   = "event-item-meta";
      meta.textContent = `${formatWhen(event.startTime, null)} \u00b7 ${event.registrationCount} registered`;

      row.appendChild(title);
      row.appendChild(meta);
      row.addEventListener("click", () => selectEvent(event.id));
      container.appendChild(row);
    });

  } catch (err) {
    setStatus(`Error loading events: ${err.message}`, true);
  }
}


function setActiveFilter(scope) {
  currentScope = scope;
  document.querySelectorAll(".filter-toggle .btn").forEach((btn) => {
    btn.classList.toggle("active", btn.dataset.scope === scope);
  });
}


// ================================================================
// Event details / registration status
// ================================================================

function clearDetailsPanel() {
  document.getElementById("event-title").textContent       = "No Event Selected";
  document.getElementById("event-badges").innerHTML        = "";
  document.getElementById("event-when-where").textContent  = "\u2014";
  document.getElementById("event-description").textContent = "\u2014";
  document.getElementById("event-registrants").innerHTML   = "";
  currentEventId = null;
}

// detail: { id, title, description, location, startTime, endTime,
//           registrationCount, isRegistered, registrants: [{id, name}, ...] }
function displayEvent(detail) {
  currentEventId = detail.id;

  document.getElementById("event-title").textContent = detail.title;

  const whenWhere = detail.location
    ? `${formatWhen(detail.startTime, detail.endTime)} \u2014 ${detail.location}`
    : formatWhen(detail.startTime, detail.endTime);
  document.getElementById("event-when-where").textContent = whenWhere;

  document.getElementById("event-description").textContent =
    detail.description || "(no description provided)";

  const badges = document.getElementById("event-badges");
  badges.innerHTML = "";

  const countBadge = document.createElement("span");
  countBadge.className   = "badge-pill badge-count";
  countBadge.textContent = `${detail.registrationCount} registered`;
  badges.appendChild(countBadge);

  const statusBadge = document.createElement("span");
  if (detail.isRegistered) {
    statusBadge.className   = "badge-pill badge-registered";
    statusBadge.textContent = "You're registered";
  } else {
    statusBadge.className   = "badge-pill badge-not-registered";
    statusBadge.textContent = "Not registered";
  }
  badges.appendChild(statusBadge);

  if (isPast(detail.startTime)) {
    const pastBadge = document.createElement("span");
    pastBadge.className   = "badge-pill badge-past";
    pastBadge.textContent = "Already happened";
    badges.appendChild(pastBadge);
  }

  renderRegistrants(detail.registrants || []);

  document.querySelectorAll("#event-list .event-item").forEach((el) => {
    el.classList.toggle("active", el.dataset.id === detail.id);
  });

  setStatus(`Viewing "${detail.title}".`);
}

function renderRegistrants(registrants) {
  const box = document.getElementById("event-registrants");
  box.innerHTML = "";

  if (registrants.length === 0) {
    box.innerHTML = '<p class="empty-state">No one has registered yet.</p>';
    return;
  }

  registrants.forEach((r) => {
    const div = document.createElement("div");
    div.className   = "friend-entry";
    div.textContent = r.name;
    box.appendChild(div);
  });
}

async function selectEvent(eventId) {
  try {
    const myName = getMyName();
    const query  = myName ? `?profileName=${encodeURIComponent(myName)}` : "";
    const detail = await api(`/${eventId}${query}`);
    displayEvent(detail);

  } catch (err) {
    setStatus(`Error loading event: ${err.message}`, true);
  }
}


// ================================================================
// Register / Cancel
// ================================================================

async function registerForEvent() {
  if (!currentEventId) {
    setStatus("Error: No event is selected. Click an event in the list first.", true);
    return;
  }
  const profileName = getMyName();
  if (!profileName) {
    setStatus("Error: Enter your profile name above before registering.", true);
    return;
  }

  try {
    const result = await apiJson(`/${currentEventId}/register`, "POST", { profileName });
    setStatus(`Registered "${result.profileName}" for "${result.eventTitle}". ${result.registrationCount} total registrations.`);
    await selectEvent(currentEventId);
    await loadEventList();

  } catch (err) {
    // Duplicate registration lands here with the server's friendly message.
    setStatus(`Error registering: ${err.message}`, true);
  }
}

async function cancelRegistration() {
  if (!currentEventId) {
    setStatus("Error: No event is selected.", true);
    return;
  }
  const profileName = getMyName();
  if (!profileName) {
    setStatus("Error: Enter your profile name above before cancelling.", true);
    return;
  }

  try {
    const result = await apiJson(`/${currentEventId}/register`, "DELETE", { profileName });
    setStatus(`Cancelled "${result.profileName}"'s registration for "${result.eventTitle}". ${result.registrationCount} total registrations.`);
    await selectEvent(currentEventId);
    await loadEventList();

  } catch (err) {
    setStatus(`Error cancelling registration: ${err.message}`, true);
  }
}


// ================================================================
// Event Listener Setup
// ================================================================

document.addEventListener("DOMContentLoaded", async () => {

  document.querySelectorAll(".filter-toggle .btn").forEach((btn) => {
    btn.addEventListener("click", async () => {
      setActiveFilter(btn.dataset.scope);
      await loadEventList();
    });
  });

  document.getElementById("btn-register").addEventListener("click", registerForEvent);
  document.getElementById("btn-cancel").addEventListener("click", cancelRegistration);

  // Re-check registration status for whichever event is open whenever
  // the name changes, so the badge/buttons reflect the right person.
  document.getElementById("input-my-name").addEventListener("change", () => {
    if (currentEventId) selectEvent(currentEventId);
  });

  clearDetailsPanel();
  await loadEventList();
  setStatus("Ready. Enter your profile name and pick an event.");
});
