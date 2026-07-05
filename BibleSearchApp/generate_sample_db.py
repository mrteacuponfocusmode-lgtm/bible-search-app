"""
Generates a small SAMPLE kjv.db so you can test the app immediately.
This is NOT the full Bible -- just ~30 well-known verses for testing.

Once you have a full, verified KJV text file, use full_kjv_import.py
instead to build the complete database.

Run with: python3 generate_sample_db.py
Output: app/src/main/assets/kjv.db
"""
import sqlite3
import os

# Renamed to kjv_v2.db so the app copies this fresh version instead of
# reusing an old cached copy from before the search was switched to
# whole-word (FTS4) matching.
OUT_PATH = os.path.join("app", "src", "main", "assets", "kjv_v2.db")
os.makedirs(os.path.dirname(OUT_PATH), exist_ok=True)

if os.path.exists(OUT_PATH):
    os.remove(OUT_PATH)

conn = sqlite3.connect(OUT_PATH)
cur = conn.cursor()
# FTS4 virtual table: indexes whole words (tokens), so a search for
# "log" will NOT match "dialogue" or "catalog" -- only the exact word.
cur.execute("""
CREATE VIRTUAL TABLE verses USING fts4(
    book, chapter, verse, text
)
""")

# (book, chapter, verse, text) -- sample only, for testing the app
sample_verses = [
    ("Genesis", 1, 1, "In the beginning God created the heaven and the earth."),
    ("Genesis", 1, 2, "And the earth was without form, and void; and darkness was upon the face of the deep. And the Spirit of God moved upon the face of the waters."),
    ("Genesis", 1, 3, "And God said, Let there be light: and there was light."),
    ("Psalms", 23, 1, "The LORD is my shepherd; I shall not want."),
    ("Psalms", 23, 2, "He maketh me to lie down in green pastures: he leadeth me beside the still waters."),
    ("Psalms", 23, 3, "He restoreth my soul: he leadeth me in the paths of righteousness for his name's sake."),
    ("Psalms", 23, 4, "Yea, though I walk through the valley of the shadow of death, I will fear no evil: for thou art with me; thy rod and thy staff they comfort me."),
    ("Psalms", 23, 5, "Thou preparest a table before me in the presence of mine enemies: thou anointest my head with oil; my cup runneth over."),
    ("Psalms", 23, 6, "Surely goodness and mercy shall follow me all the days of my life: and I will dwell in the house of the LORD for ever."),
    ("Proverbs", 3, 5, "Trust in the LORD with all thine heart; and lean not unto thine own understanding."),
    ("Proverbs", 3, 6, "In all thy ways acknowledge him, and he shall direct thy paths."),
    ("Matthew", 5, 3, "Blessed are the poor in spirit: for theirs is the kingdom of heaven."),
    ("Matthew", 5, 4, "Blessed are they that mourn: for they shall be comforted."),
    ("Matthew", 5, 5, "Blessed are the meek: for they shall inherit the earth."),
    ("Matthew", 5, 6, "Blessed are they which do hunger and thirst after righteousness: for they shall be filled."),
    ("John", 3, 16, "For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life."),
    ("John", 3, 17, "For God sent not his Son into the world to condemn the world; but that the world through him might be saved."),
    ("Romans", 8, 28, "And we know that all things work together for good to them that love God, to them who are the called according to his purpose."),
    ("Philippians", 4, 13, "I can do all things through Christ which strengtheneth me."),
    ("1 Corinthians", 13, 4, "Charity suffereth long, and is kind; charity envieth not; charity vaunteth not itself, is not puffed up,"),
    ("1 Corinthians", 13, 5, "Doth not behave itself unseemly, seeketh not her own, is not easily provoked, thinketh no evil;"),
    ("1 Corinthians", 13, 6, "Rejoiceth not in iniquity, but rejoiceth in the truth;"),
    ("1 Corinthians", 13, 7, "Beareth all things, believeth all things, hopeth all things, endureth all things."),
]

cur.executemany(
    "INSERT INTO verses (book, chapter, verse, text) VALUES (?, ?, ?, ?)",
    sample_verses,
)
conn.commit()
conn.close()
print(f"Sample database created at {OUT_PATH} with {len(sample_verses)} verses.")
