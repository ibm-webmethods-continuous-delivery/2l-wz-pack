# webMethods Utilities Packs

This repository contains a collection of webMethods utilities, mainly centered around Integration Server utilization.

Currently, this is WIP - work in progress as a certain category of Wx packages are ported to a new expression in the "Wz" family.

As some of these packages are prerequisites for other repos, we we will keep some intermediary commits as we progress.

## Organization and Naming Conventions

All packages in this repo are prefixed with capital `Z`. The second letter is one of:

- `x`: Marking packages that are intended for exclusive development time use
- `y`: Marking packages that are intended for test time use, for example in automated CI pipelines
- `z`: Marking packages that are intended for production use

`Zx*` packages MAY depend on `Zy*` and `Zz*` packages.
`Zy*` packages MAY depend on `Zz*` packages.

`Zz*` package MUST not depend on `Zx*` or `Zy*` packages.
`Zy*` package MUST not depend on `Zx*` packages.

## Packages Layers

### Independent Production Packages

#### ZzWrappers

Wrappers around existing IS / MSR services that do not properly declare their signature. This package exists exclusively for this purpose.

**Note**: If you think this should be corrected in the product, please open an issue in the current repo. Although this is not an issue of this repo, it may be worth tracking the need, which will be eventually passed through to product management.

Developers may deploy diretly in production OR may substitute the flow call with the following naming convention

`zz.wrapper.<serviceNS> -> <serviceNS>`

Whenever the signature changes with the product evolution, the wrapper MAY be versioned, like in the example below:

`zz.wrapper.v10.<serviceNS> -> <serviceNS>`

or

`zz.wrapper.v1003.<serviceNS> -> <serviceNS>`

All services in this package MUST respect the above naming convention.

This package reflects services that the user may discover in WmPublic and WmRoot packages using Designer, and sometimes services that can be discovered by analysing WmRoot and WmPublic dsp files, by looking at %invoke % tags and their surroundings. These can be also discovered by using Developer Tools when using the browser based administration UI. USe these with care as they are not officially documented and may change in time.

**Note**: we are doing this exceptionally whenever the product does not provide a proper automation primitive to cover for tha fundamental IWCD principles, for example for relentless automation. If you think this should be corrected in the product, please open an issue in the current repo. Although this is not an issue of this repo, it may be worth tracking the need, which will be eventually passed through to product management.

