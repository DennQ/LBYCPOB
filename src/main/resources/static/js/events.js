// js/events.js
// ================================================================
// Member 3: Event Discovery & Registration.
// The browser only ever talks to this server's own /api/events,
// /api/registrations and /api/profiles endpoints.
//
// This app has no login system, so "who am I" is resolved by typing
// a profile name into the box on the left, which we look up via
// GET /api/profiles/lookup?query=... to get a real profile UUID.
// That UUID is what actually gets sent to the registration endpoints
// (the backend has no way to identify "you" otherwise).
// ================================================================

const EVENTS_API = "/api/events";
const REG_API     = "/api/registrations";
const PROFILES_API = "/api/profiles";

let currentEventId = null;
let currentScope    = "upcoming";
let allProfiles      = []; // cached for id -> name lookups (registrant list)


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

function getMyNameTyped() {
  return document.getElementById("input-my-name").value.trim();
}

function formatWhen(eventDate) {
  const d = new Date(eventDate);
  return d.toLocaleString(undefined, { dateStyle: "medium", timeStyle: "short" });
}

function isPast(eventDate) {
  return new Date(eventDate).getTime() < Date.now();
}

async function apiGet(url) {
  const response = await fetch(url);
  const rawText  = await response.text();
  let body = null;
  if (rawText) {
    try { body = JSON.parse(rawText); }
    catch { throw new Error(`Server returned HTTP ${response.status} (not JSON).`); }
  }
  if (!response.ok) {
    throw new Error((body && body.error) || `Server error ${response.status}.`);
  }
  return body;
}

async function apiJson(url, method, payload) {
  const response = await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
  const rawText = await response.text();
  let body = null;
  if (rawText) {
    try { body = JSON.parse(rawText); }
    catch { throw new Error(`Server returned HTTP ${response.status} (not JSON).`); }
  }
  if (!response.ok) {
    throw new Error((body && body.error) || `Server error ${response.status}.`);
  }
  return body;
}

/** Resolves the typed name to a real profile, or throws a friendly error. */
async function resolveMyProfile() {
  const name = getMyNameTyped();
  if (!name) {
    throw new Error("Enter your profile name above first.");
  }
  try {
    return await apiGet(`${PROFILES_API}/lookup?query=${encodeURIComponent(name)}`);
  } catch {
    throw new Error(`No profile named "${name}" exists. Add that profile first on the Profiles page.`);
  }
}

async function loadAllProfiles() {
  try {
    allProfiles = await apiGet(PROFILES_API);
  } catch {
    allProfiles = [];
  }
}

function profileNameFor(profileId) {
  const match = allProfiles.find((p) => p.id === profileId);
  return match ? match.name : profileId.substring(0, 8) + "\u2026";
}


// ================================================================
// Event list (Upcoming / Next 2 Weeks / All)
// ================================================================

async function loadEventList() {
  try {
    const events = await apiGet(`${EVENTS_API}?scope=${currentScope}`);

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
      title.textContent = event.name;

      const meta = document.createElement("div");
      meta.className   = "event-item-meta";
      meta.textContent = `${formatWhen(event.eventDate)} \u00b7 ${event.organizationName} \u00b7 ${event.registrationCount} registered`;

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

// detail: { id, name, description, venue, eventDate, capacity, organizationId,
//           organizationName, registrationCount, isRegistered }
async function displayEvent(detail) {
  currentEventId = detail.id;

  document.getElementById("event-title").textContent = detail.name;

  const venuePart = detail.venue ? ` \u2014 ${detail.venue}` : "";
  document.getElementById("event-when-where").textContent =
    `${formatWhen(detail.eventDate)}${venuePart} \u00b7 Hosted by ${detail.organizationName}`;

  document.getElementById("event-description").textContent =
    detail.description || "(no description provided)";

  const badges = document.getElementById("event-badges");
  badges.innerHTML = "";

  const countBadge = document.createElement("span");
  countBadge.className   = "badge-pill badge-count";
  const capacityText = detail.capacity ? ` / ${detail.capacity}` : "";
  countBadge.textContent = `${detail.registrationCount}${capacityText} registered`;
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

  if (isPast(detail.eventDate)) {
    const pastBadge = document.createElement("span");
    pastBadge.className   = "badge-pill badge-past";
    pastBadge.textContent = "Already happened";
    badges.appendChild(pastBadge);
  }

  document.querySelectorAll("#event-list .event-item").forEach((el) => {
    el.classList.toggle("active", el.dataset.id === detail.id);
  });

  await loadRegistrants(detail.id);
  setStatus(`Viewing "${detail.name}".`);
}

async function loadRegistrants(eventId) {
  const box = document.getElementById("event-registrants");
  try {
    const registrations = await apiGet(`${REG_API}/event/${eventId}`);
    box.innerHTML = "";

    if (registrations.length === 0) {
      box.innerHTML = '<p class="empty-state">No one has registered yet.</p>';
      return;
    }

    if (allProfiles.length === 0) await loadAllProfiles();

    registrations.forEach((r) => {
      const div = document.createElement("div");
      div.className   = "friend-entry";
      div.textContent = profileNameFor(r.profileId);
      box.appendChild(div);
    });
  } catch (err) {
    box.innerHTML = '<p class="empty-state">Could not load registrants.</p>';
  }
}

async function selectEvent(eventId) {
  try {
    let query = "";
    const typedName = getMyNameTyped();
    if (typedName) {
      try {
        const me = await resolveMyProfile();
        query = `?profileId=${me.id}`;
      } catch {
        // Unknown name typed -- still show the event, just without
        // "isRegistered" personalization. Don't block browsing on a typo.
      }
    }
    const detail = await apiGet(`${EVENTS_API}/${eventId}${query}`);
    await displayEvent(detail);

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

  try {
    const me = await resolveMyProfile();
    const result = await apiJson(`${REG_API}`, "POST", { eventId: currentEventId, profileId: me.id });
    setStatus(`Registered "${me.name}" for this event.`);
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

  try {
    const me = await resolveMyProfile();
    await fetch(`${REG_API}/${currentEventId}/${me.id}`, { method: "DELETE" })
      .then(async (res) => {
        if (!res.ok) {
          const body = await res.json().catch(() => ({}));
          throw new Error(body.error || `Server error ${res.status}.`);
        }
      });
    setStatus(`Cancelled "${me.name}"'s registration for this event.`);
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

  document.getElementById("input-my-name").addEventListener("change", () => {
    if (currentEventId) selectEvent(currentEventId);
  });

  clearDetailsPanel();
  await loadAllProfiles();
  await loadEventList();
  setStatus("Ready. Enter your profile name and pick an event.");
});
