# Offline KJV Bible Search — Android App
### (No Android Studio install needed — built for free on GitHub's servers)

A simple, fully offline Bible search app. No internet permission, no ads,
no tracking — just a search box and instant, whole-word-accurate results,
working on Android 5.0 (API 21) and up.

This project is now a **complete, ready-to-build Android project**. You
don't need to install anything on your computer — GitHub will compile it
into an installable `.apk` file for you, for free, in the cloud.

## What you need
- A free GitHub account: https://github.com/join
- A web browser
- That's it.

## Step 1 — Create a repository
1. Go to https://github.com/new
2. Repository name: `bible-search-app`
3. Choose **Public** (this makes GitHub's build servers free/unlimited
   for you — Private repos also work but have a monthly free-minutes cap).
4. Click **Create repository**. Don't add a README/gitignore, leave it empty.

## Step 2 — Upload this whole project
1. On your new (empty) repo's page, click **"uploading an existing file"**
   (or Add file → Upload files).
2. Open the folder you extracted from `BibleSearchApp.zip` on your
   computer, select **everything inside it** (all files and folders:
   `app`, `.github`, `build.gradle`, `settings.gradle`, etc.) and drag
   them all into the GitHub upload box in your browser.
   - Chrome/Edge preserve folder structure when you drag folders in —
     make sure `.github` and `app` show up as folders in the upload
     preview, not flattened.
   - If your browser won't let you drag a hidden folder like `.github`,
     that's fine — you can also click "Add file → Create new file" and
     type the path `.github/workflows/build-apk.yml` directly, then
     paste in that file's contents from the zip.
3. Scroll down, write a commit message like "Add app", click
   **Commit changes**.

## Step 3 — Let GitHub build it
1. Click the **Actions** tab at the top of your repo.
2. You should see a workflow run start automatically (triggered by
   your upload). If not, click "Build APK" on the left, then
   **Run workflow**.
3. Wait 2-5 minutes. A green checkmark means it succeeded.

## Step 4 — Download your APK
1. Click into the finished workflow run.
2. Scroll to the **Artifacts** section at the bottom.
3. Download `bible-search-app-debug-apk` — it's a zip containing
   `app-debug.apk`.

## Step 5 — Install it on your phone
1. Get that `.apk` onto your Android phone — easiest ways: email it
   to yourself, upload to Google Drive and download from Drive on
   the phone, or plug the phone in with a USB cable and copy it over.
2. Tap the `.apk` file on your phone to install it.
3. Android will ask to allow installing from this source (since it's
   not from the Play Store) — allow it, then Install.
4. Open "Bible Search KJV" — type a word and you'll get instant,
   fully offline, whole-word-accurate results.

## Step 6 — Swap in the full Bible text (currently only 23 sample verses)
1. Find a full plain-text KJV file on your computer (public domain in
   the US — search "KJV plain text bible download"), formatted like:
   `Genesis 1:1 In the beginning God created the heaven and the earth.`
2. This step needs Python on your computer to run the conversion
   script (`full_kjv_import.py`, included). If you don't have Python
   either, tell Claude — there's a way to do this step through GitHub
   too, it just takes a bit more setup.
3. Run: `python3 full_kjv_import.py path/to/that_file.txt`
   This regenerates `app/src/main/assets/kjv_v2.db` with the complete text.
4. Upload the updated `kjv_v2.db` file to your GitHub repo (Add file →
   Upload files, drop it into the same `app/src/main/assets/` folder,
   overwriting the old one) → commit → Actions will rebuild
   automatically → download the new APK from Step 4.

## How search works
Search uses SQLite's FTS4 full-text index — **whole-word/phrase
matching only**. Searching "log" will not match "dialogue" or
"catalog". Searching "the Lord is my shepherd" only matches that
exact phrase.

## If something goes wrong
- Red ❌ on the Actions run → click into it, click the failed step, and
  paste the error text back to Claude — these are almost always a
  one-line fix.
- If you'd rather install Android Studio later after all, the same
  project folder opens directly in Android Studio too (File → Open →
  select the `BibleSearchApp` folder) — no changes needed.
