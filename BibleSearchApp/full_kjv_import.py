"""
Converts a full plain-text KJV Bible file into the kjv.db database
used by the Android app, replacing the small sample database.

WHERE TO GET THE TEXT:
The King James Version is public domain in the United States (no US
copyright). You can find free plain-text or CSV/JSON copies from many
open Bible-text projects online -- search for "KJV plain text bible"
or "KJV bible csv" and pick a reputable source. Download it yourself
and point this script at the file.

EXPECTED INPUT FORMAT (one verse per line):
    Genesis 1:1 In the beginning God created the heaven and the earth.
    Genesis 1:2 And the earth was without form, and void...
    ...
(Book name, space, chapter:verse, space, verse text)

If your downloaded file uses a different format (e.g. CSV with
columns book,chapter,verse,text), tell Claude what the format looks
like and it can adjust this script for you in seconds.

Usage:
    python3 full_kjv_import.py path/to/kjv_full.txt
"""
import sqlite3
import os
import re
import sys

LINE_PATTERN = re.compile(r"^((?:[1-3]\s)?[A-Za-z ]+?)\s+(\d+):(\d+)\s+(.*)$")

def main():
    if len(sys.argv) != 2:
        print("Usage: python3 full_kjv_import.py path/to/kjv_full.txt")
        sys.exit(1)

    input_path = sys.argv[1]
    out_path = os.path.join("app", "src", "main", "assets", "kjv_v2.db")
    os.makedirs(os.path.dirname(out_path), exist_ok=True)

    if os.path.exists(out_path):
        os.remove(out_path)

    conn = sqlite3.connect(out_path)
    cur = conn.cursor()
    # FTS4 virtual table -> whole-word ("exact word") search, not
    # substring matching.
    cur.execute("""
        CREATE VIRTUAL TABLE verses USING fts4(
            book, chapter, verse, text
        )
    """)

    count = 0
    skipped = 0
    with open(input_path, encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            m = LINE_PATTERN.match(line)
            if not m:
                skipped += 1
                continue
            book, chapter, verse, text = m.groups()
            cur.execute(
                "INSERT INTO verses (book, chapter, verse, text) VALUES (?, ?, ?, ?)",
                (book.strip(), int(chapter), int(verse), text.strip()),
            )
            count += 1

    conn.commit()
    conn.close()
    print(f"Imported {count} verses into {out_path}.")
    if skipped:
        print(f"Skipped {skipped} lines that didn't match the expected format.")
        print("If that number looks too high, show Claude a few sample lines "
              "from your source file so the pattern can be fixed.")

if __name__ == "__main__":
    main()
