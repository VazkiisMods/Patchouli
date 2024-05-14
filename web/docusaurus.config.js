const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').DocusaurusConfig} */
module.exports = {
  title: 'Patchouli',
  tagline: 'Documentation for Everyone',
  url: 'https://vazkiimods.github.io',
  baseUrl: '/Patchouli/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/favicon.ico',
  organizationName: 'VazkiiMods', // Usually your GitHub org/user name.
  projectName: 'Patchouli', // Usually your repo name.
  themeConfig: {
    navbar: {
      title: 'Patchouli',
      logo: {
        alt: 'Patchouli Logo',
        src: 'img/logo.svg',
      },
      items: [
        {
          type: 'doc',
          docId: 'intro',
          position: 'left',
          label: 'Docs',
        },
        {to: '/blog', label: 'Blog', position: 'left'},
        {
          href: 'https://github.com/VazkiiMods/Patchouli',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Docs',
          items: [
            {
              label: 'Tutorial',
              to: '/docs/intro',
            },
          ],
        },
        {
          title: 'Community',
          items: [
            {
              label: 'Forums',
              href: 'https://forum.violetmoon.org/t/patchouli',
            },
            {
              label: 'Discord',
              href: 'https://discord.com/invite/vm',
            },
            {
              label: 'Twitter',
              href: 'https://twitter.com/VazkiiMods',
            },
          ],
        },
        {
          title: 'More',
          items: [
            {
              label: 'Blog',
              to: '/blog',
            },
            {
              label: 'GitHub',
              href: 'https://github.com/VazkiiMods/Patchouli',
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} Vazkii's Mods Developers. Built with Docusaurus.`,
    },
    prism: {
      theme: lightCodeTheme,
      darkTheme: darkCodeTheme,
    },
  },
  presets: [
    [
      '@docusaurus/preset-classic',
      {
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          editUrl: 'https://github.com/VazkiiMods/Patchouli/edit/1.18.x/web/',
        },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          editUrl:
            'https://github.com/VazkiiMods/Patchouli/edit/1.18.x/web/blog/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      },
    ],
  ],
};
