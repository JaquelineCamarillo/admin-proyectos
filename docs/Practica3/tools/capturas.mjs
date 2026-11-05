import { chromium } from 'playwright-core';
import path from 'path';
import fs from 'fs';
import crypto from 'crypto';
import { PNG } from 'pngjs';

const OUT_DIR = 'C:\\admin-proyectos\\docs\\Practica3\\capturas';
const JENKINS_URL = process.env.JENKINS_URL || 'http://localhost:9090';
const SONAR_URL = process.env.SONAR_URL || 'http://localhost:9000';
const BACKEND_URL = process.env.BACKEND_URL || 'http://localhost:8081';
const FRONTEND_URL = process.env.FRONTEND_URL || 'http://localhost:8080';

const JENKINS_USER = process.env.JENKINS_USER || 'admin';
const JENKINS_PASS = process.env.JENKINS_PASS || 'Admin123!';
const SONAR_USER = process.env.SONAR_USER || 'admin';
const SONAR_PASS = process.env.SONAR_PASS || 'Admin123!';

// Limpiar capturas viejas
if (fs.existsSync(OUT_DIR)) {
  console.log('Borrando capturas viejas en:', OUT_DIR);
  for (const f of fs.readdirSync(OUT_DIR)) {
    if (f.endsWith('.png')) {
      fs.unlinkSync(path.join(OUT_DIR, f));
    }
  }
} else {
  fs.mkdirSync(OUT_DIR, { recursive: true });
}

async function dismissSonarBanners(page) {
  try {
    const dismissBtns = page.locator('button:has-text("Dismiss"), button:has-text("Dismiss notification"), button[aria-label="Dismiss"], button[aria-label="Close"], button.banner-close, .modal-close');
    const count = await dismissBtns.count();
    for (let i = 0; i < count; i++) {
      const btn = dismissBtns.nth(i);
      if (await btn.isVisible().catch(() => false)) {
        await btn.click().catch(() => {});
      }
    }
  } catch (_) {}
}

async function waitForItem(page, item, timeout = 60000) {
  if (item === '0 failures') {
    const loc = page.getByText('All tests are passing', { exact: false })
      .or(page.getByText('0 failures', { exact: false }))
      .or(page.getByText('All Tests', { exact: false }))
      .first();
    await loc.waitFor({ state: 'attached', timeout });
    return;
  }
  const loc = page.getByText(item, { exact: false })
    .or(page.locator(`input[value="${item}"]`))
    .or(page.locator(`[value="${item}"]`))
    .first();
  await loc.waitFor({ state: 'attached', timeout });
}

async function captureWithRetry(page, filename, url, options = {}) {
  const { selector, text, fullPage = false, scrollSelector, scrollText, bottom = false } = options;
  const dest = path.join(OUT_DIR, filename);

  for (let attempt = 1; attempt <= 3; attempt++) {
    try {
      console.log(`\n[${filename}] (Intento ${attempt}/3) Cargando ${url}...`);
      await page.goto(url, { waitUntil: 'networkidle', timeout: 60000 });

      // En SonarQube, esperar a que desaparezcan spinners y cerrar banners
      if (url.includes(':9000')) {
        await page.locator('.spinner, [data-testid="spinner"], .loading').waitFor({ state: 'detached', timeout: 25000 }).catch(() => {});
        await dismissSonarBanners(page);
      }

      if (selector) {
        console.log(`[${filename}] Esperando selector: ${selector}`);
        await page.locator(selector).first().waitFor({ state: 'visible', timeout: 60000 });
      }

      if (text) {
        const textList = Array.isArray(text) ? text : [text];
        for (const t of textList) {
          console.log(`[${filename}] Esperando texto o valor: "${t}"`);
          await waitForItem(page, t, 60000);
        }
      }

      // Espera obligatoria de 2 segundos más tras resolver selectores
      await page.waitForTimeout(2000);

      if (scrollText) {
        console.log(`[${filename}] Haciendo scroll hacia texto/valor: "${scrollText}"`);
        const loc = page.getByText(scrollText, { exact: false })
          .or(page.locator(`input[value="${scrollText}"]`))
          .or(page.locator(`[value="${scrollText}"]`))
          .first();
        await loc.scrollIntoViewIfNeeded({ timeout: 8000 }).catch(() => {});
        await page.evaluate(() => window.scrollBy(0, -120));
      } else if (scrollSelector) {
        console.log(`[${filename}] Haciendo scroll hacia selector: ${scrollSelector}`);
        const loc = page.locator(scrollSelector).first();
        await loc.scrollIntoViewIfNeeded({ timeout: 8000 }).catch(() => {});
        await page.evaluate(() => window.scrollBy(0, -120));
      } else if (bottom) {
        console.log(`[${filename}] Haciendo scroll al final de la página`);
        await page.evaluate(() => window.scrollTo(0, document.body.scrollHeight));
      }

      await page.waitForTimeout(1000);

      if (fullPage) {
        await page.screenshot({ path: dest, fullPage: true });
      } else {
        await page.screenshot({ path: dest });
      }

      console.log(`[${filename}] CAPTURADA OK`);
      return;
    } catch (err) {
      console.error(`[${filename}] Error en intento ${attempt}:`, err.message.split('\n')[0]);
      if (attempt === 3) {
        throw new Error(`[${filename}] Falló la captura tras 3 intentos: ${err.message}`);
      }
      await page.waitForTimeout(3000);
    }
  }
}

// Verificación de imágenes generadas
function verifyImage(filepath) {
  const buf = fs.readFileSync(filepath);
  const md5 = crypto.createHash('md5').update(buf).digest('hex');
  const png = PNG.sync.read(buf);

  // Muestrear píxeles para calcular variabilidad de color
  const sampleStep = Math.max(1, Math.floor((png.width * png.height) / 5000));
  const colors = new Set();
  let rSum = 0, gSum = 0, bSum = 0, count = 0;

  for (let i = 0; i < png.data.length; i += 4 * sampleStep) {
    const r = png.data[i];
    const g = png.data[i + 1];
    const b = png.data[i + 2];
    colors.add(`${r},${g},${b}`);
    rSum += r;
    gSum += g;
    bSum += b;
    count++;
  }

  const isMonochrome = colors.size < 10;
  return {
    filepath,
    width: png.width,
    height: png.height,
    md5,
    colorsCount: colors.size,
    isMonochrome
  };
}

(async () => {
  console.log('Iniciando navegador Chromium / Edge (1600x1000, deviceScaleFactor 1)...');
  const browser = await chromium.launch({
    channel: 'msedge',
    headless: true
  });

  const context = await browser.newContext({
    viewport: { width: 1600, height: 1000 },
    deviceScaleFactor: 1,
    locale: 'es-MX'
  });

  const page = await context.newPage();

  // 1. LOGIN JENKINS
  console.log('1. Autenticando en Jenkins...');
  await page.goto(`${JENKINS_URL}/login`, { waitUntil: 'networkidle', timeout: 60000 });
  await page.fill('input[name="j_username"]', JENKINS_USER);
  await page.fill('input[name="j_password"]', JENKINS_PASS);
  await Promise.all([
    page.waitForURL(url => !url.toString().includes('/login'), { timeout: 30000 }),
    page.locator('button[type="submit"], button[name="Submit"], input[type="submit"]').first().click()
  ]);
  console.log('Jenkins autenticado exitosamente. URL:', page.url());
  await page.waitForTimeout(2000);

  // 2. LOGIN SONARQUBE
  console.log('2. Autenticando en SonarQube...');
  await page.goto(`${SONAR_URL}/sessions/new`, { waitUntil: 'networkidle', timeout: 60000 });
  await page.fill('#login', SONAR_USER);
  await page.fill('#password', SONAR_PASS);
  await page.locator('button[type="submit"]').first().click();
  await page.waitForSelector('a:has-text("Projects"), a[href="/projects"]', { state: 'visible', timeout: 45000 });
  await dismissSonarBanners(page);
  console.log('SonarQube autenticado exitosamente. URL:', page.url());
  await page.waitForTimeout(2000);

  // CAPTURAS OFICIALES REQUERIDAS
  const captures = [
    {
      file: 'f01_tools.png',
      url: `${JENKINS_URL}/manage/configureTools/`,
      text: ['jdk21', 'maven-3.9'],
      fullPage: true
    },
    {
      file: 'f02_sonar_server.png',
      url: `${JENKINS_URL}/manage/configure`,
      text: 'http://sonarqube:9000',
      scrollText: 'http://sonarqube:9000'
    },
    {
      file: 'f03_junit_backend.png',
      url: `${JENKINS_URL}/job/pipeline-backend/lastSuccessfulBuild/testReport/`,
      text: '0 failures'
    },
    {
      file: 'f04_junit_frontend.png',
      url: `${JENKINS_URL}/job/pipeline-frontend/lastSuccessfulBuild/testReport/`,
      text: '0 failures'
    },
    {
      file: 'f05_jacoco_backend.png',
      url: 'file:///C:/admin-proyectos/Ejercicio5MyAPIGestor/target/site/jacoco/index.html',
      text: 'Total'
    },
    {
      file: 'f06_jacoco_frontend.png',
      url: 'file:///C:/admin-proyectos/Ejercicio5Cliente/target/site/jacoco/index.html',
      text: 'Total'
    },
    {
      file: 'f07_sonar_projects.png',
      url: `${SONAR_URL}/projects`,
      text: ['Ejercicio5Cliente', 'Ejercicio5MyAPIGestor']
    },
    {
      file: 'f08_sonar_backend.png',
      url: `${SONAR_URL}/dashboard?id=Ejercicio5MyAPIGestor`,
      text: ['Passed', 'Coverage']
    },
    {
      file: 'f09_sonar_frontend.png',
      url: `${SONAR_URL}/dashboard?id=Ejercicio5Cliente`,
      text: ['Passed', 'Coverage']
    },
    {
      file: 'f10_sonar_cov_backend.png',
      url: `${SONAR_URL}/component_measures?id=Ejercicio5MyAPIGestor&metric=coverage`,
      text: 'Coverage'
    },
    {
      file: 'f10b_sonar_vuln_backend.png',
      url: `${SONAR_URL}/project/issues?id=Ejercicio5MyAPIGestor&types=VULNERABILITY&resolved=false`,
      selector: '.issue-list, .issues-counter, [data-testid="issues-counter"], [data-testid="no-issues"], .search-navigator, main, #issues-page, body'
    },
    {
      file: 'f11_trivy_antes.png',
      url: 'file:///C:/admin-proyectos/docs/Practica3/seguridad/trivy_antes.html',
      text: 'Report',
      fullPage: true
    },
    {
      file: 'f12_trivy_despues.png',
      url: 'file:///C:/admin-proyectos/docs/Practica3/seguridad/trivy_despues.html',
      text: 'Report',
      fullPage: true
    },
    {
      file: 'f13_stages_backend.png',
      url: `${JENKINS_URL}/job/pipeline-backend/`,
      selector: '.stage-row, .pipeline-graph, #pipeline-box, .stage-view, #main-panel'
    },
    {
      file: 'f14_console_backend_inicio.png',
      url: `${JENKINS_URL}/job/pipeline-backend/lastSuccessfulBuild/console`,
      text: 'openjdk version',
      scrollText: 'openjdk version'
    },
    {
      file: 'f14b_console_backend_fin.png',
      url: `${JENKINS_URL}/job/pipeline-backend/lastSuccessfulBuild/console`,
      text: 'Finished: SUCCESS',
      bottom: true
    },
    {
      file: 'f15_stages_frontend.png',
      url: `${JENKINS_URL}/job/pipeline-frontend/`,
      selector: '.stage-row, .pipeline-graph, #pipeline-box, .stage-view, #main-panel'
    },
    {
      file: 'f16_console_frontend_inicio.png',
      url: `${JENKINS_URL}/job/pipeline-frontend/lastSuccessfulBuild/console`,
      text: 'openjdk version',
      scrollText: 'openjdk version'
    },
    {
      file: 'f16b_console_frontend_fin.png',
      url: `${JENKINS_URL}/job/pipeline-frontend/lastSuccessfulBuild/console`,
      text: 'Finished: SUCCESS',
      bottom: true
    },
    {
      file: 'f17_stages_database.png',
      url: `${JENKINS_URL}/job/pipeline-database/`,
      selector: '.stage-row, .pipeline-graph, #pipeline-box, .stage-view, #main-panel'
    },
    {
      file: 'f18_db_estructura.png',
      url: `${JENKINS_URL}/job/pipeline-database/lastSuccessfulBuild/console`,
      text: '=== 1. TABLAS EN DBGESTOR ===',
      scrollText: '=== 1. TABLAS EN DBGESTOR ==='
    },
    {
      file: 'f19_db_crud.png',
      url: `${JENKINS_URL}/job/pipeline-database/lastSuccessfulBuild/console`,
      text: '=== PRUEBAS CRUD SOBRE DBGESTOR ===',
      scrollText: '=== PRUEBAS CRUD SOBRE DBGESTOR ==='
    },
    {
      file: 'f20_github_commits.png',
      url: 'https://github.com/JaquelineCamarillo/admin-proyectos/commits/main',
      selector: '[data-testid="commit-row-item"], .TimelineItem, #repo-content-pjax-container'
    },
    {
      file: 'f21_swagger.png',
      url: `${BACKEND_URL}/swagger-ui/index.html`,
      selector: '.opblock'
    },
    {
      file: 'f22_frontend.png',
      url: `${FRONTEND_URL}/`,
      selector: 'body'
    },
    {
      file: 'f23_jenkins_dashboard.png',
      url: `${JENKINS_URL}/`,
      text: ['pipeline-backend', 'pipeline-frontend', 'pipeline-database']
    }
  ];

  for (const c of captures) {
    await captureWithRetry(page, c.file, c.url, c);
  }

  await browser.close();
  console.log('\n--- TODAS LAS CAPTURAS FUERON GENERADAS CON ÉXITO ---');

  // VERIFICACIÓN AUTOMATIZADA DE CADA ARCHIVO
  console.log('\n--- INICIANDO VERIFICACIÓN DE IMÁGENES ---');
  const results = [];
  const md5Map = new Map();

  const files = fs.readdirSync(OUT_DIR).filter(f => f.endsWith('.png')).sort();
  for (const f of files) {
    const fullpath = path.join(OUT_DIR, f);
    const info = verifyImage(fullpath);
    if (info.isMonochrome) {
      console.error(`ALERTA: La imagen ${f} parece monocromática (posible pantalla blanca o en negro).`);
    }
    if (md5Map.has(info.md5)) {
      console.error(`ALERTA: Imagen duplicada detectada: ${f} tiene el mismo hash que ${md5Map.get(info.md5)}`);
    } else {
      md5Map.set(info.md5, f);
    }
    results.push(info);
  }

  console.log(`\nVerificación final: ${results.length} imágenes verificadas.`);
})();
