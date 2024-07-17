# Entry JSON Format

This page details every key you can have in an Entry json file.

The "entry ID" of an entry is the path necessary to
get to it from `/en_us/entries`. So if your entry is in
`/en_us/entries/misc/cool_stuff.json`, its ID would be `patchouli:misc/cool_stuff`.

For modders, the domain used is the domain in which the book is defined.

## **name** (String, _mandatory_)

The name of this entry.

## **category** (String, _mandatory_)

The category this entry belongs to. This must be set to one of your categories' ID. For
best results, use a fully-qualified ID that includes your book namespace
`yourbooknamespace:categoryname`. In the future this will be enforced.

## **icon** (String, _mandatory_)

The icon for this entry. This can either be an [ItemStack
String](/docs/patchouli-advanced/itemstack-format), if you want an item to be the icon, or
a resource location pointing to a square texture. If you want to use a resource location,
make sure to end it with .png

## **pages** (Object Array, _mandatory_)

The array of pages for this entry. In the following format:

```
[
    {
        "type": "TYPE",
        (type specific data ...)
    },
    {
        "type": "TYPE",
        (type specific data...)
    }
    (...)
]
```

See [Default Page Types](/docs/patchouli-basics/page-types) for the page types that
Patchouli comes with and what data each one requires, or [Using
Templates](/docs/patchouli-basics/templates) for instructions on how to create your own.

## **advancement** (String)

The name of the advancement you want this entry to be locked behind. See [Locking Content
with Advancements](/docs/patchouli-basics/advancement-locking) for more info on locking
content.

## **flag** (String)

A config flag expression that determines whether this entry should exist or not. See
[Using Config Flags](/docs/patchouli-basics/config-gating) for more info on config flags.

## **priority** (boolean)

Defaults to false. If set to true, the entry will show up with an italicized name, and
will always show up at the top of the category. Use for really important entries you want
to show up at the top.

## **secret** (boolean)

Defaults to false. Set this to true to make this entry a secret entry. Secret entries
don't display as "Locked" when locked, and instead will not display at all until
unlocked. Secret entries do not count for % completion of the book, and when unlocked will
instead show as an additional line in the tooltip.

## **read_by_default** (boolean)

Defaults to false. Set this to true if you want to entry to not show the unread ("(!!)")
indicator if it hasn't been opened yet.

## **sortnum** (integer)

The sorting number for this entry. Defaults to 0. Entries with the same sorting number are
sorted alphabetically, whereas entries with different sorting numbers are sorted from
lowest to highest. Priority entries always show up first.

It's recommended you do *not* use this, as breaking the alphabetical sorting order can make
things confusing, but it's left as an option.

## **turnin** (String)

The ID of an advancement the player needs to do to "complete" this entry. The entry will
show up at the top of the list with a (?) icon next to it until this advancement is
complete. This can be used as a quest system or simply to help guide players along a
starting path.

## **extra_recipe_mappings** (Object String -> Int)

Additional list of items this page teaches the crafting process for, for use with the
in-world right click and quick lookup feature. Keys are ItemStack strings, values are
0-indexed page numbers.

## **entry_color** (String)

The color of the link to this entry from its category, in hex ("RRGGBB", # not necessary). Defaults to [`book.text_color`](book-json.md#text_color-string).
