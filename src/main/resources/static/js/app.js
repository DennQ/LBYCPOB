// ================================================================
// STUDENT SOCIALNET - PROFILE MANAGEMENT
// ================================================================

const API_BASE = "/api/profiles";

const DEFAULT_AVATAR =
    "https://6fkrqtkwbcnqsois.public.blob.vercel-storage.com/avatars/default.webp";

let currentProfileId = null;


// ================================================================
// HELPERS
// ================================================================

function setStatus(message, isError = false) {

  const bar =
      document.getElementById("status-message");

  const footer =
      document.getElementById("status-bar");

  bar.textContent = message;

  footer.style.background =
      isError
          ? "#6b1a1a"
          : "var(--clr-status-bg)";

  footer.style.color =
      isError
          ? "#ffcccc"
          : "var(--clr-status-text)";
}


function clearCentrePanel() {

  document.getElementById("profile-pic").src =
      DEFAULT_AVATAR;

  document.getElementById("profile-name").textContent =
      "No Student Selected";

  document.getElementById("profile-student-id").textContent =
      "\u2014";

  document.getElementById("profile-course").textContent =
      "\u2014";

  document.getElementById("profile-year-level").textContent =
      "\u2014";

  document.getElementById("profile-status").textContent =
      "\u2014";

  document.getElementById("profile-quote").textContent =
      "\u2014";

  document.getElementById("friends-list").innerHTML =
      "";

  clearStudentEditFields();

  currentProfileId = null;
}


function clearStudentEditFields() {

  document.getElementById("edit-student-id").value =
      "";

  document.getElementById("edit-course").value =
      "";

  document.getElementById("edit-year-level").value =
      "";
}


// ================================================================
// DISPLAY STUDENT
// ================================================================

function displayProfile(profile) {

  document.getElementById("profile-pic").src =
      profile.picture || DEFAULT_AVATAR;

  document.getElementById("profile-name").textContent =
      profile.name;

  document.getElementById("profile-student-id").textContent =
      profile.studentId || "(not set)";

  document.getElementById("profile-course").textContent =
      profile.course || "(not set)";

  document.getElementById("profile-year-level").textContent =
      profile.yearLevel ?? "(not set)";

  document.getElementById("profile-status").textContent =
      profile.status || "(no status set)";

  document.getElementById("profile-quote").textContent =
      profile.quote || "(no quote set)";


  // Pre-fill edit form
  document.getElementById("edit-student-id").value =
      profile.studentId || "";

  document.getElementById("edit-course").value =
      profile.course || "";

  document.getElementById("edit-year-level").value =
      profile.yearLevel ?? "";


  currentProfileId =
      profile.id;

  renderFriendsList(
      profile.friends || []
  );

  setStatus(
      `Displaying student ${profile.name}.`
  );
}


// ================================================================
// FRIEND LIST
// ================================================================

function renderFriendsList(friends) {

  const box =
      document.getElementById("friends-list");

  box.innerHTML =
      "";

  if (friends.length === 0) {

    box.innerHTML =
        '<p class="empty-state">No friends yet.</p>';

    return;
  }

  friends.forEach((friend) => {

    const div =
        document.createElement("div");

    div.className =
        "friend-entry";

    div.textContent =
        friend.name;

    box.appendChild(div);
  });
}


// ================================================================
// UPLOAD PROGRESS
// ================================================================

function showUploadProgress(label = "Uploading...") {

  const wrapper =
      document.getElementById("upload-progress");

  const text =
      document.getElementById("upload-progress-label");

  text.textContent =
      label;

  wrapper.hidden =
      false;
}


function hideUploadProgress() {

  document.getElementById(
      "upload-progress"
  ).hidden = true;
}


// ================================================================
// API HELPERS
// ================================================================

async function api(path, options = {}) {

  const response =
      await fetch(
          API_BASE + path,
          options
      );

  const rawText =
      await response.text();

  let body =
      null;

  if (rawText) {

    try {

      body =
          JSON.parse(rawText);

    } catch {

      throw new Error(
          `Server returned HTTP ${response.status} (not JSON).`
      );
    }
  }

  if (!response.ok) {

    throw new Error(
        (body && body.error)
        ||
        `Server error ${response.status}.`
    );
  }

  return body;
}


function apiJson(
    path,
    method,
    payload
) {

  return api(
      path,
      {
        method,

        headers: {
          "Content-Type":
              "application/json"
        },

        body:
            JSON.stringify(payload)
      }
  );
}


// ================================================================
// LOAD STUDENTS
// ================================================================

async function loadProfileList() {

  try {

    const profiles =
        await api("");

    const container =
        document.getElementById(
            "profile-list"
        );

    container.innerHTML =
        "";

    if (profiles.length === 0) {

      container.innerHTML =
          '<p class="empty-state">No students found.</p>';

      return;
    }


    profiles.forEach((profile) => {

      const row =
          document.createElement("div");

      row.className =
          "profile-item";

      row.dataset.id =
          profile.id;


      const img =
          document.createElement("img");

      img.className =
          "list-thumb";

      img.src =
          profile.picture ||
          DEFAULT_AVATAR;

      img.alt =
          profile.name;

      img.onerror =
          () => {

            img.src =
                DEFAULT_AVATAR;
          };


      const textWrapper =
          document.createElement("div");

      textWrapper.style.overflow =
          "hidden";


      const name =
          document.createElement("div");

      name.textContent =
          profile.name;

      name.style.fontWeight =
          "600";


      const studentId =
          document.createElement("small");

      studentId.textContent =
          profile.studentId
          ||
          "No Student ID";

      studentId.style.display =
          "block";

      studentId.style.opacity =
          "0.7";


      textWrapper.appendChild(
          name
      );

      textWrapper.appendChild(
          studentId
      );


      row.appendChild(
          img
      );

      row.appendChild(
          textWrapper
      );


      row.addEventListener(
          "click",
          () =>
              selectProfile(
                  profile.id
              )
      );


      container.appendChild(
          row
      );
    });

  } catch (err) {

    setStatus(
        `Error loading students: ${err.message}`,
        true
    );
  }
}


// ================================================================
// SELECT STUDENT
// ================================================================

async function selectProfile(profileId) {

  try {

    document
        .querySelectorAll(
            "#profile-list .profile-item"
        )
        .forEach((element) => {

          element.classList.toggle(
              "active",
              element.dataset.id === profileId
          );
        });


    const profile =
        await api(
            `/${profileId}`
        );


    displayProfile(
        profile
    );

  } catch (err) {

    setStatus(
        `Error selecting student: ${err.message}`,
        true
    );
  }
}


// ================================================================
// CREATE STUDENT
// ================================================================

async function addProfile() {

  const name =
      document
          .getElementById("input-name")
          .value
          .trim();

  const studentId =
      document
          .getElementById("input-student-id")
          .value
          .trim();

  const course =
      document
          .getElementById("input-course")
          .value
          .trim();

  const yearLevelText =
      document
          .getElementById("input-year-level")
          .value
          .trim();

  const yearLevel =
      Number(yearLevelText);


  if (!name) {

    setStatus(
        "Error: Enter the student's name.",
        true
    );

    return;
  }


  if (!studentId) {

    setStatus(
        "Error: Enter the student ID.",
        true
    );

    return;
  }


  if (!course) {

    setStatus(
        "Error: Enter the student's course.",
        true
    );

    return;
  }


  if (
      !yearLevelText ||
      Number.isNaN(yearLevel) ||
      yearLevel <= 0
  ) {

    setStatus(
        "Error: Enter a valid year level.",
        true
    );

    return;
  }


  try {

    const created =
        await apiJson(
            "",
            "POST",
            {
              name,
              studentId,
              course,
              yearLevel
            }
        );


    document.getElementById(
        "input-name"
    ).value = "";

    document.getElementById(
        "input-student-id"
    ).value = "";

    document.getElementById(
        "input-course"
    ).value = "";

    document.getElementById(
        "input-year-level"
    ).value = "";


    await loadProfileList();


    document
        .querySelectorAll(
            "#profile-list .profile-item"
        )
        .forEach((element) => {

          element.classList.toggle(
              "active",
              element.dataset.id === created.id
          );
        });


    displayProfile(
        created
    );


    setStatus(
        `Student "${name}" created successfully.`
    );

  } catch (err) {

    setStatus(
        `Error adding student: ${err.message}`,
        true
    );
  }
}


// ================================================================
// SEARCH STUDENT
// ================================================================

async function lookUpProfile() {

  const query =
      document
          .getElementById("input-search")
          .value
          .trim();


  if (!query) {

    setStatus(
        "Error: Enter a student name or student ID.",
        true
    );

    return;
  }


  try {

    const profile =
        await api(
            `/lookup?query=${encodeURIComponent(query)}`
        );


    document
        .querySelectorAll(
            "#profile-list .profile-item"
        )
        .forEach((element) => {

          element.classList.toggle(
              "active",
              element.dataset.id === profile.id
          );
        });


    displayProfile(
        profile
    );

  } catch (err) {

    setStatus(
        err.message,
        true
    );

    clearCentrePanel();
  }
}


// ================================================================
// UPDATE STUDENT INFORMATION
// ================================================================

async function updateStudentInfo() {

  if (!currentProfileId) {

    setStatus(
        "Error: No student is selected.",
        true
    );

    return;
  }


  const studentId =
      document
          .getElementById("edit-student-id")
          .value
          .trim();

  const course =
      document
          .getElementById("edit-course")
          .value
          .trim();

  const yearLevelText =
      document
          .getElementById("edit-year-level")
          .value
          .trim();

  const yearLevel =
      Number(yearLevelText);


  if (!studentId) {

    setStatus(
        "Error: Student ID cannot be empty.",
        true
    );

    return;
  }


  if (!course) {

    setStatus(
        "Error: Course cannot be empty.",
        true
    );

    return;
  }


  if (
      !yearLevelText ||
      Number.isNaN(yearLevel) ||
      yearLevel <= 0
  ) {

    setStatus(
        "Error: Enter a valid year level.",
        true
    );

    return;
  }


  try {

    const updated =
        await apiJson(
            `/${currentProfileId}/student-info`,
            "PATCH",
            {
              studentId,
              course,
              yearLevel
            }
        );


    displayProfile(
        updated
    );

    await loadProfileList();


    document
        .querySelectorAll(
            "#profile-list .profile-item"
        )
        .forEach((element) => {

          element.classList.toggle(
              "active",
              element.dataset.id === currentProfileId
          );
        });


    setStatus(
        "Student information updated successfully."
    );

  } catch (err) {

    setStatus(
        `Error updating student information: ${err.message}`,
        true
    );
  }
}


// ================================================================
// DELETE STUDENT
// ================================================================

async function deleteProfile() {

  if (!currentProfileId) {

    setStatus(
        "Error: No student is selected.",
        true
    );

    return;
  }


  const name =
      document
          .getElementById("profile-name")
          .textContent;


  if (
      !window.confirm(
          `Delete the student profile for "${name}"? This cannot be undone.`
      )
  ) {

    setStatus(
        "Deletion cancelled."
    );

    return;
  }


  try {

    await api(
        `/${currentProfileId}`,
        {
          method:
              "DELETE"
        }
    );


    clearCentrePanel();

    await loadProfileList();


    setStatus(
        `Student "${name}" deleted.`
    );

  } catch (err) {

    setStatus(
        `Error deleting student: ${err.message}`,
        true
    );
  }
}


// ================================================================
// UPDATE STATUS
// ================================================================

async function changeStatus() {

  if (!currentProfileId) {

    setStatus(
        "Error: No student is selected.",
        true
    );

    return;
  }


  const newStatus =
      document
          .getElementById("input-status")
          .value
          .trim();


  if (!newStatus) {

    setStatus(
        "Error: Status field is empty.",
        true
    );

    return;
  }


  try {

    await apiJson(
        `/${currentProfileId}/status`,
        "PATCH",
        {
          status:
          newStatus
        }
    );


    document
        .getElementById(
            "profile-status"
        )
        .textContent =
        newStatus;


    document
        .getElementById(
            "input-status"
        )
        .value =
        "";


    setStatus(
        "Status updated."
    );

  } catch (err) {

    setStatus(
        `Error updating status: ${err.message}`,
        true
    );
  }
}


// ================================================================
// UPDATE QUOTE
// ================================================================

async function changeQuote() {

  if (!currentProfileId) {

    setStatus(
        "Error: No student is selected.",
        true
    );

    return;
  }


  const newQuote =
      document
          .getElementById("input-quote")
          .value
          .trim();


  if (!newQuote) {

    setStatus(
        "Error: Quote field is empty.",
        true
    );

    return;
  }


  try {

    await apiJson(
        `/${currentProfileId}/quote`,
        "PATCH",
        {
          quote:
          newQuote
        }
    );


    document
        .getElementById(
            "profile-quote"
        )
        .textContent =
        newQuote;


    document
        .getElementById(
            "input-quote"
        )
        .value =
        "";


    setStatus(
        "Favorite quote updated."
    );

  } catch (err) {

    setStatus(
        `Error updating quote: ${err.message}`,
        true
    );
  }
}


// ================================================================
// PROFILE PICTURE
// ================================================================

async function changePicture() {

  if (!currentProfileId) {

    setStatus(
        "Error: No student is selected.",
        true
    );

    return;
  }


  const fileInput =
      document.getElementById(
          "input-picture-file"
      );

  const urlInput =
      document.getElementById(
          "input-picture-url"
      );


  const file =
      fileInput.files[0];

  const urlValue =
      urlInput.value.trim();


  if (file) {

    await uploadAvatarFile(
        file
    );

    return;
  }


  if (urlValue) {

    await savePictureUrl(
        urlValue
    );

    return;
  }


  setStatus(
      "Error: Select a file or enter a URL.",
      true
  );
}


// ================================================================
// PICTURE UPLOAD
// ================================================================

async function uploadAvatarFile(file) {

  if (
      !file.type.startsWith(
          "image/"
      )
  ) {

    setStatus(
        "Error: The selected file is not an image.",
        true
    );

    return;
  }


  showUploadProgress(
      "Compressing and uploading..."
  );

  setStatus(
      "Uploading image..."
  );


  try {

    const formData =
        new FormData();

    formData.append(
        "file",
        file
    );


    const response =
        await fetch(
            `${API_BASE}/${currentProfileId}/avatar`,
            {
              method:
                  "POST",

              body:
              formData
            }
        );


    const rawText =
        await response.text();

    let result;


    try {

      result =
          JSON.parse(
              rawText
          );

    } catch {

      throw new Error(
          `Server returned HTTP ${response.status} (not JSON).`
      );
    }


    if (!response.ok) {

      throw new Error(
          result.error
          ||
          `Server error ${response.status}.`
      );
    }


    applyNewPicture(
        result.url
    );


    document
        .getElementById(
            "input-picture-file"
        )
        .value =
        "";


    setStatus(
        "Picture updated successfully."
    );

  } catch (err) {

    setStatus(
        "Error uploading image: " +
        err.message,
        true
    );

  } finally {

    hideUploadProgress();
  }
}


// ================================================================
// PICTURE URL
// ================================================================

async function savePictureUrl(url) {

  if (
      !url.startsWith(
          "https://"
      )
  ) {

    setStatus(
        "Error: URL must start with https://",
        true
    );

    return;
  }


  setStatus(
      "Saving picture URL..."
  );


  try {

    await apiJson(
        `/${currentProfileId}/picture`,
        "PATCH",
        {
          pictureUrl:
          url
        }
    );


    applyNewPicture(
        url
    );


    document
        .getElementById(
            "input-picture-url"
        )
        .value =
        "";


    setStatus(
        "Picture updated successfully."
    );

  } catch (err) {

    setStatus(
        `Error saving URL: ${err.message}`,
        true
    );
  }
}


function applyNewPicture(url) {

  document
      .getElementById(
          "profile-pic"
      )
      .src =
      url;


  const activeThumb =
      document.querySelector(
          "#profile-list .profile-item.active .list-thumb"
      );


  if (activeThumb) {

    activeThumb.src =
        url;
  }
}


// ================================================================
// ADD FRIEND
// ================================================================

async function addFriend() {

  if (!currentProfileId) {

    setStatus(
        "Error: No student is selected.",
        true
    );

    return;
  }


  const friendName =
      document
          .getElementById("input-friend")
          .value
          .trim();


  if (!friendName) {

    setStatus(
        "Error: Friend name field is empty.",
        true
    );

    return;
  }


  try {

    const result =
        await apiJson(
            `/${currentProfileId}/friends`,
            "POST",
            {
              friendName
            }
        );


    document
        .getElementById(
            "input-friend"
        )
        .value =
        "";


    await selectProfile(
        currentProfileId
    );


    setStatus(
        `"${result.friendName}" added as a friend.`
    );

  } catch (err) {

    setStatus(
        `Error adding friend: ${err.message}`,
        true
    );
  }
}


// ================================================================
// REMOVE FRIEND
// ================================================================

async function removeFriend() {

  if (!currentProfileId) {

    setStatus(
        "Error: No student is selected.",
        true
    );

    return;
  }


  const friendName =
      document
          .getElementById("input-friend")
          .value
          .trim();


  if (!friendName) {

    setStatus(
        "Error: Friend name field is empty.",
        true
    );

    return;
  }


  try {

    const result =
        await apiJson(
            `/${currentProfileId}/friends`,
            "DELETE",
            {
              friendName
            }
        );


    document
        .getElementById(
            "input-friend"
        )
        .value =
        "";


    await selectProfile(
        currentProfileId
    );


    setStatus(
        `"${result.friendName}" removed from friends.`
    );

  } catch (err) {

    setStatus(
        `Error removing friend: ${err.message}`,
        true
    );
  }
}


// ================================================================
// EVENT LISTENERS
// ================================================================

document.addEventListener(
    "DOMContentLoaded",
    async () => {


      // CREATE STUDENT
      document
          .getElementById("btn-add")
          .addEventListener(
              "click",
              addProfile
          );


      // SEARCH STUDENT
      document
          .getElementById("btn-lookup")
          .addEventListener(
              "click",
              lookUpProfile
          );


      document
          .getElementById("input-search")
          .addEventListener(
              "keydown",
              (event) => {

                if (
                    event.key ===
                    "Enter"
                ) {

                  lookUpProfile();
                }
              }
          );


      // DELETE
      document
          .getElementById("btn-delete")
          .addEventListener(
              "click",
              deleteProfile
          );


      // UPDATE STUDENT INFO
      document
          .getElementById("btn-student-info")
          .addEventListener(
              "click",
              updateStudentInfo
          );


      // STATUS
      document
          .getElementById("btn-status")
          .addEventListener(
              "click",
              changeStatus
          );


      document
          .getElementById("input-status")
          .addEventListener(
              "keydown",
              (event) => {

                if (
                    event.key ===
                    "Enter"
                ) {

                  changeStatus();
                }
              }
          );


      // QUOTE
      document
          .getElementById("btn-quote")
          .addEventListener(
              "click",
              changeQuote
          );


      document
          .getElementById("input-quote")
          .addEventListener(
              "keydown",
              (event) => {

                if (
                    event.key ===
                    "Enter"
                ) {

                  changeQuote();
                }
              }
          );


      // PROFILE PICTURE
      document
          .getElementById("btn-picture")
          .addEventListener(
              "click",
              changePicture
          );


      document
          .getElementById("input-picture-file")
          .addEventListener(
              "change",
              (event) => {

                const file =
                    event.target.files[0];


                if (!file) {

                  return;
                }


                if (
                    !file.type.startsWith(
                        "image/"
                    )
                ) {

                  setStatus(
                      "Error: Selected file is not an image.",
                      true
                  );

                  return;
                }


                const pic =
                    document.getElementById(
                        "profile-pic"
                    );


                if (
                    pic.dataset.previewUrl
                ) {

                  URL.revokeObjectURL(
                      pic.dataset.previewUrl
                  );
                }


                const previewUrl =
                    URL.createObjectURL(
                        file
                    );


                pic.src =
                    previewUrl;

                pic.dataset.previewUrl =
                    previewUrl;


                setStatus(
                    "Preview loaded. Click 'Update Picture' to save."
                );
              }
          );


      // FRIEND MANAGEMENT
      document
          .getElementById("btn-add-friend")
          .addEventListener(
              "click",
              addFriend
          );


      document
          .getElementById("btn-remove-friend")
          .addEventListener(
              "click",
              removeFriend
          );


      // EXIT
      document
          .getElementById("btn-exit")
          .addEventListener(
              "click",
              () => {

                setStatus(
                    "To exit, close this browser tab."
                );

                window.close();
              }
          );


      // INITIAL LOAD
      await loadProfileList();


      setStatus(
          "Ready. Select a student from the list or add a new student."
      );
    }
);