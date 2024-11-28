# Patchouli
Accessible, Data-Driven, Dependency-Free Documentation for Minecraft Modders and Pack Makers

For more information, see the [docs](https://vazkiimods.github.io/Patchouli/docs/intro).

## What does the mod's name mean???!
https://en.touhouwiki.net/wiki/Patchouli_Knowledge

## Maven info

Maven artifacts are located [here](https://maven.blamejared.com/vazkii/patchouli/Patchouli/), each folder representing a version.

Note: As of 1.16, intermediate (non-release) Maven builds are no longer persisted.
That is, you must either depend on a *released* version of Patchouli, e.g. `1.16-37` or `1.16-37-FABRIC`, or specifically opt in to the bleeding-edge
build of the next version. For example, `1.16-38-SNAPSHOT` or `1.16-38-FABRIC-SNAPSHOT` would be the current bleeding edge version of future version `1.16-38`. 

Note that `-SNAPSHOT` versions can be broken from time to time, and you are strongly discouraged from using them unless you are helping dogfood, test, or contribute to Patchouli. They may also be pruned from time to time to save disk space on the server. Do *not* rely on `-SNAPSHOT` versions for anything important!

In Fabric, add the following to your `build.gradle`
```gradle
repositories {
    maven { url 'https://maven.blamejared.com' }
}

dependencies {
    modImplementation "vazkii.patchouli:Patchouli:[VERSION]"
}
```

In Forge, use the following:
```gradle
repositories {
    maven { url 'https://maven.blamejared.com' }
}

dependencies {
    compileOnly fg.deobf("vazkii.patchouli:Patchouli:[VERSION]:api")
    runtimeOnly fg.deobf("vazkii.patchouli:Patchouli:[VERSION]")
}
```

Note: Any code not located in the package `vazkii.patchouli.api` is strictly implementation detail, and you should not rely on it as it will change without warning.

## Mixin Troubleshooting (Forge only)
Patchouli uses Mixin to implement some of its features. On Forge, the game might crash when trying to launch in-dev, as ForgeGradle does not remap the refmap by itself. This can be worked around by specifying the refmap remapping manually: add [these lines](https://github.com/SpongePowered/Mixin/issues/462#issuecomment-791370319) to your build.gradle and regenerate your run configurations in the IDE afterwards.
MixinGradle applies this fix automatically - if you are using Mixin in your project you shouldn't have to change anything.

## License Information

Patchouli's original code and assets are licensed under the CC-BY-NC-SA 3.0 Unported
license.  We recognize that this is not ideal, and are open to changing the licensing of
the code in the future.

Please note that this mod uses official Mojang mappings (Mojmap). If you depend on
Patchouli as normal, or only consume Patchouli's API, there should be no licensing
concerns, as the mod is remapped to Intermediary (or SRG, for Forge) on compile.

There is a license concern, however, if you bundle Patchouli with your mod using
Jar-in-Jar.  Building a mod which uses Mixin inserts a refmap, which for Patchouli will
contain raw Mojang mappings in a JSON file.  If this presents a licensing problem to you,
then do not bundle Patchouli and just depend on it externally.  I recommend using normal
dependencies either way, as Jar-in-Jar inflates your archive sizes to store a mod that
will probably be in most modpacks anyways.

## Developer Info
### Repository Layout
From 1.18 onwards, Patchouli is developed with Fabric and Forge in the same branch of the
same repository. This is a boon for productivity as most code can be shared without
tedious merging of commits back and forth between branches. All code uses Mojang mappings
(MojMap).

This scheme is based on the [Multi-Loader
Template](https://github.com/jaredlll08/MultiLoader-Template) created by @jaredlll08 and
@Darkhax. Many thanks to them!

How it works is we have three Gradle subprojects: `Xplat`, `Forge`, and `Fabric`.
`Xplat` contains code that is loader-agnostic. In the IDE, we set up this subproject
using Sponge's `VanillaGradle` plugin, which sets up a basic Mojmap-mapped game JAR to aid
in auto-complete, etc. while coding.  However, this subproject is not actually compiled on
its own.

Instead, the loader-specific subprojects `Forge` and `Fabric` include the source of
`Xplat` into their own sources when compiling. The loader-specific subprojects use the
native loader's tools (ForgeGradle and Loom, respectively), so in nearly all respects this
is the same as copying and pasting the `Xplat` code into the loader-specific subproject.

If a loader needs to be temporarily disabled, simply comment it out in `settings.gradle`.

### Making a Release
1. Pull from remote, test all changes, and commit everything.
2. `git tag -a release-<VERSION>`. All Patchouli versions *must* follow the version format
   `<MC-VER>-INT`, so it'll probably look like `git tag -a release-1.17.1-55`. You can
   check which number is the next one by looking at `gradle.properties`.
3. In the Git editor that pops up, write the changelog. Finish the tag process (usually by
   saving and closing the editor).
4. Increment the build number in `gradle.properties` of the next release. Commit this
   separately.
5. Push the branch and tag: `git push origin <branch> <tag>`
6. Wait a bit and the binaries should magically be published to GitHub, CurseForge, and Modrinth for you

## Signing
Releases starting from 1.19.4-79 are signed with the Violet Moon signing key, see [this
page](https://github.com/VazkiiMods/.github/blob/main/security/README.md) for information
about how to verify the artifacts.
