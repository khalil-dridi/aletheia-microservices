/*
 * Simple automated visual/accessibility audit using Puppeteer and axe-core.
 * Run with: node tools/audit-visual.js http://localhost:4200/profile
 * It will crawl a page, inject axe and report contrast/label/structure issues.
 */
const puppeteer = require('puppeteer');
const axeSource = require('axe-core').source;

(async () => {
  const url = process.argv[2] || 'http://localhost:4200';
  console.log(`Auditing ${url}`);
  const browser = await puppeteer.launch();
  const page = await browser.newPage();
  await page.goto(url, { waitUntil: 'networkidle0' });
  await page.addScriptTag({ content: axeSource });
  const results = await page.evaluate(async () => {
    return await axe.run({
      runOnly: ['wcag2aa', 'contrast'],
    });
  });
  console.log(JSON.stringify(results.violations, null, 2));
  await browser.close();
})();
