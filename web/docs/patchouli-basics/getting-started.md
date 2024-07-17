---
sidebar_position: 1
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Getting Started

This entry serves as a quick guide of what to do to get started making your own Patchouli
books, read on and follow carefully!

### 0. Install Patchouli
Pretty obvious. For modders, you can load it as a lib to your mod project or just drop it
in your workspace's mods folder. The preferred way is to use maven, which you find the
address for in the repo's readme. For non-modders, obtain the mod through the usual
distribution channels.

### 1. Locate patchouli_books
Your books and their contents will go in your `patchouli_books` directory, so you need to
find it.

* Modpack authors can use the external folder `.minecraft/patchouli_books`, located next
  to the `mods` folder. If it doesn't exist, create it.
* For modders, it'll be `/data/_YOURMODID_/patchouli_books` in your resources, you'll have
  to make it yourself. You will also need to create a mirroring
  `/assets/_YOURMODID_/patchouli_books` directory as well.

### 2. Create your folder structure
Once you find `patchouli_books`, decide on a name for your book. Only lower case characters
and underscores are allowed. This is the internal name for your book, and we encourage you
to make it distinctive. For mods, you should name it after what the book is for
(e.g. `lexicon` for Botania). For modpacks, name it something with your modpack's name,
for example `crucial_2_guide_book`. After you have a name, create folders and files inside
patchouli_books so that it looks like this:

* `patchouli_books`
    * `<the name you just chose>` (folder)
        * `book.json` (empty file)
        * `en_us` (folder)
            * `entries` (empty folder)
            * `categories` (empty folder)
            * `templates` (empty folder)

You may have as many books as you want, even within the same mod(pack). Each book has a
[resource location](https://minecraft.wiki/w/Resource_location). If the book is part of a
mod, the namespace is that mod's ID. If the book is loaded from the
`.minecraft/patchouli_books` folder, the namespace is `patchouli`. The second part of the
namespaced ID is the name you chose above. So for Botania, the full book ID is
`botania:lexicon`.

Note that creating an `en_us` folder means you're creating the "English" version of your
book. The contents you put in the `en_us` folder are always the "main" ones loaded, so
even if your book isn't meant to be natively in english, you need to put your main stuff
there.

Any translators may create folders with their languages and override any files they
wish. They're automatically loaded if the game language is changed. For translators:
Please don't include in your folder anything you aren't overriding.

### 3. Populate book.json
`book.json` is what tells Patchouli that a book exists. It should be located in one of the
following places, as described above:

* `/data/_YOURMODID_/patchouli_books/_YOURBOOKNAME_/book.json`
* `.minecraft/patchouli_books/_YOURBOOKNAME_/book.json`

Open it up using a text editor, and fill it in as follows:

```json
{
	"name": "My First Book!",
	"landing_text": "Welcome to Patchouli",
	"version": 1,
	"creative_tab": "minecraft:tools_and_utilities",
	"use_resource_pack": true
}
```

The `name` and `landing_text` values show up on the book UI on the landing UI.  The
`version` field specifies which edition your book is. Whenever you update your book, you
should also update the edition value. For modders, you can use localization keys in both
`name` and `landing_text`.

`use_resource_pack` must be set to true for books declared in `/data/`, but is optional
for books declared in `.minecraft/`. Setting the flag requires that the book's contents
(see step 5) are loaded through the resource pack system.

For more customization options, please read [Book JSON
Format](/docs/reference/book-json). (highly recommended!)

### 4. Check ingame
Load your game and check if your book is there. Unless you specified otherwise, it should
be in the Tools and Utilities creative tab, but you can also search for it. Opening the
book should yield a blank landing page.

![Screenshot of blank Patchouli book on landing page](/img/gettingStarted.png)

If you don't see it, check if Patchouli is properly loaded and if there's any errors in
your log.

Everything after this can be hot loaded without closing the game, so feel free to keep it
open as you do further edits.

### 5. Add Stub Content
Time to add some content to your book!

Go to your *book content folder*. If `use_resource_pack` is set to `true`, this will be
`/assets/_YOURBOOKNAMESPACE_/patchouli_books/_YOURBOOKNAME_`, within a resource pack (or
mod resources). Otherwise, it is the same folder as `book.json`.

Within that folder, create the following structure:
* en_us
    * entries (folder)
        * test (folder)
            * test_entry.json (empty file)
    * categories (folder)
        * test_category.json (empty file)
    * templates (empty folder)

Open `test_entry.json` and `test_category.json` and fill them in as follows:

<Tabs groupId="book-type">
<TabItem value="mod" label="Mod" default>

```json title="test_entry.json"
{
    "name": "Test Entry",
    "icon": "minecraft:writable_book",
    "category": "your_book_namespace_change_me:test_category",
    "pages": [
        {
            "type": "patchouli:text",
            "text": "This is a test entry, but it should show up!"
        }
    ]
}
```

</TabItem>
<TabItem value="modpack" label="Modpack">

```json title="test_entry.json"
{
    "name": "Test Entry",
    "icon": "minecraft:writable_book",
    "category": "patchouli:test_category",
    "pages": [
        {
            "type": "patchouli:text",
            "text": "This is a test entry, but it should show up!"
        }
    ]
}
```

</TabItem>
</Tabs>

```json title="test_category.json"
{
    "name": "Test Category",
    "description": "This is a test category for testing!",
    "icon": "minecraft:writable_book"
}
```

You'll need to edit the "category" key in the test entry to have the right namespace.

Save your files, then return ingame and open your book. Shift-click the pencil in the
bottom-left corner. When you do so, it will reload the book contents, and you should see
the category and entry you just defined appear.

:::caution Important note for modpack authors

The namespace is always `patchouli` for IDs of books/categories/entries in
`.minecraft/patchouli_books` (i.e. modpack books). For example, with the above structure,
you would need to use the following IDs:

* Book: `patchouli:_YOURBOOKNAME_`
* Category: `patchouli:test_category`
* Entry: `patchouli:test_entry`

:::

### 6. Learn More!

You're done getting set up, now it's time to learn more about what you can do with
Patchouli's book system. Check the following pages out:

* [Locking Content with Advancements](/docs/patchouli-basics/advancement-locking)
* [Text Formatting 101](/docs/patchouli-basics/text-formatting)
* [Book JSON Format](/docs/reference/book-json)
* [Category JSON Format](/docs/reference/category-json)
* [Entry JSON Format](/docs/reference/entry-json)
    * [Default Page Types](/docs/patchouli-basics/page-types)
* [Using Templates](/docs/patchouli-basics/templates)

### 7. Use the Book Item

Here's a few examples of how to use your book's item. As a refresher, your book's
*namespace* is your mod id, if you're a modder, and `patchouli`, if you're a pack
maker. Then, if your folder is called "coolbook" then your *book ID* is
`yourbooknamespace:coolbook`.

**CraftTweaker**:  
`<patchouli:guide_book>.withTag({"patchouli:book": "YOURBOOKID"});`  
or just use `/ct hand`

**Vanilla Recipes/Advancements**:
```json
"item": "patchouli:guide_book",
"nbt": {
    "patchouli:book": "YOURBOOKID"
}
```

### 8. Some Pointers

* For modpack makers, if you want to use your own images, textures, sounds, or other
  assets, you'll need a tool to load them in, such as [Open
  Loader](https://www.curseforge.com/minecraft/mc-mods/open-loader).
* To grant your book to new players automatically, see [this
  page](/docs/patchouli-basics/giving-new)
* You need to reload the game to load new book.json files, but not the book contents.
* Book contents are purely client sided, whereas book.json files are loaded by the server
  too.
* You don't have to put your entries in folders corresponding to the categories they
  belong in, but it helps with organization!
* No dependencies are needed if you're a modder, not even any code. Patchouli
  automatically finds your files if you put them in the right place.

### Appendix A: Example file structures

#### For a modder

Your `src/main/resources/` should look like this:
```
.
├── assets
│   └── your_mod_id_change_me
│       └── patchouli_books
│           └── your_book_name_change_me
│               ├── en_us
│               │   ├── categories
│               │   ├── entries
│               │   └── templates
│               └── zh_cn
│                   └── entries
└── data
    └── your_mod_id_change_me
        └── patchouli_books
            └── your_book_name_change_me
                └── book.json
```

#### For an addon modder, or modpacker modifying another mod's book
Ship the following in a resource pack, containing the files you'd like to add or override in the target book:

```
.
└── assets
    └── mod_id_of_the_book_change_me
        └── patchouli_books
            └── the_book_name_to_modify_change_me
                ├── en_us
                │   ├── categories
                │   ├── entries
                │   └── templates
                └── zh_cn
                    └── entries
```

Note: For modders, resource loading order caveats apply. Please be careful when overriding
content.

#### For modpackers creating a new book
Your `.minecraft` should look like this:

```
.minecraft
└── patchouli_books
    └── your_book_name_change_me
        ├── book.json
        └── en_us
            ├── categories
            └── entries
```
