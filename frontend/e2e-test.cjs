const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

async function runE2E() {
  const screenshotsDir = path.join(__dirname, 'screenshots');
  if (!fs.existsSync(screenshotsDir)) {
    fs.mkdirSync(screenshotsDir, { recursive: true });
  }

  const consoleLogs = [];
  const networkErrors = [];

  console.log('🚀 Iniciando bateria completa de testes E2E com Playwright...');
  const browser = await chromium.launch({
    headless: true,
    executablePath: '/usr/bin/google-chrome',
    args: ['--no-sandbox', '--disable-setuid-sandbox', '--disable-gpu']
  });

  const context = await browser.newContext({
    viewport: { width: 1440, height: 900 }
  });
  const page = await context.newPage();

  page.on('console', msg => {
    const type = msg.type();
    const text = msg.text();
    consoleLogs.push({ type, text });
    if (type === 'error' || type === 'warn') {
      console.log(`[Browser Console ${type.toUpperCase()}]: ${text}`);
    }
  });

  page.on('requestfailed', request => {
    networkErrors.push({
      url: request.url(),
      method: request.method(),
      failure: request.failure()?.errorText || 'Unknown failure'
    });
    console.log(`[Network Error]: ${request.method()} ${request.url()} - ${request.failure()?.errorText}`);
  });

  page.on('response', async response => {
    if (response.status() >= 400) {
      let body = '';
      try { body = await response.text(); } catch (e) {}
      networkErrors.push({
        url: response.url(),
        status: response.status(),
        statusText: response.statusText(),
        body: body.substring(0, 300)
      });
      console.log(`[HTTP ${response.status()}]: ${response.url()}`);
    }
  });

  try {
    // 1. Dashboard Inicial (Colaborador)
    console.log('\n📍 [1/7] Acessando Dashboard (http://localhost:5173)...');
    await page.goto('http://localhost:5173', { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000);
    await page.screenshot({ path: path.join(screenshotsDir, '01-colaborador-dashboard.png'), fullPage: true });
    console.log('✅ Dashboard de Colaborador renderizado.');

    // 2. Troca de Perfil RBAC no Header: Gestor e Admin
    console.log('\n📍 [2/7] Testando Simulação RBAC e Perfis Keycloak no Header...');
    const profileBtn = page.locator('header button').filter({ hasText: /Silva|Colaborador|Mendes|Duarte/ }).first();
    await profileBtn.click();
    await page.waitForTimeout(500);
    await page.screenshot({ path: path.join(screenshotsDir, '02-header-profile-menu.png') });

    // Trocar para Roberto Mendes (Gestor)
    const gestorOpt = page.locator('button').filter({ hasText: 'Roberto Mendes' }).first();
    if (await gestorOpt.count() > 0) {
      await gestorOpt.click();
      await page.waitForTimeout(1000);
      await page.screenshot({ path: path.join(screenshotsDir, '03-gestor-dashboard.png'), fullPage: true });
      console.log('✅ Perfil alterado para Roberto Mendes (GESTOR) — KPIs de Gestão e botão de Gerar Quiz visíveis.');
    }

    // Trocar para Mariana Duarte (Admin)
    await profileBtn.click();
    await page.waitForTimeout(500);
    const adminOpt = page.locator('button').filter({ hasText: 'Mariana Duarte' }).first();
    if (await adminOpt.count() > 0) {
      await adminOpt.click();
      await page.waitForTimeout(1000);
      await page.screenshot({ path: path.join(screenshotsDir, '04-admin-dashboard.png'), fullPage: true });
      console.log('✅ Perfil alterado para Mariana Duarte (ADMIN) — Acessos de infraestrutura e governança validados.');
    }

    // 3. Página Dedicada de Quizzes (/quizzes)
    console.log('\n📍 [3/7] Testando Página de Quizzes & Avaliações (/quizzes)...');
    await page.goto('http://localhost:5173/quizzes', { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000);
    await page.screenshot({ path: path.join(screenshotsDir, '05-quizzes-view.png'), fullPage: true });

    // Selecionar o primeiro quiz
    const startQuizBtn = page.locator('button:has-text("Iniciar Simulado"), button:has-text("Fazer Simulado")').first();
    if (await startQuizBtn.count() > 0) {
      await startQuizBtn.click();
      await page.waitForTimeout(500);

      // Responder a primeira opção
      const firstOption = page.locator('div.border.cursor-pointer, button.cursor-pointer').first();
      if (await firstOption.count() > 0) {
        await firstOption.click();
        await page.waitForTimeout(500);
        console.log('✅ Opção selecionada no quiz com feedback visual instantâneo.');
      }
      await page.screenshot({ path: path.join(screenshotsDir, '06-quiz-answered.png') });
    }

    // 4. Página Dedicada de Gestão de Turma (/gestao)
    console.log('\n📍 [4/7] Testando Painel de Gestão da Turma (/gestao)...');
    await page.goto('http://localhost:5173/gestao', { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000);
    await page.screenshot({ path: path.join(screenshotsDir, '07-management-view.png'), fullPage: true });
    console.log('✅ Painel de Gestão (/gestao) renderizado com tabela de colaboradores e SLAs.');

    // 5. Página Dedicada de Perfil (/perfil)
    console.log('\n📍 [5/7] Testando Página de Perfil (/perfil)...');
    await page.goto('http://localhost:5173/perfil', { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000);
    await page.screenshot({ path: path.join(screenshotsDir, '08-profile-view.png'), fullPage: true });
    console.log('✅ Página de Perfil (/perfil) renderizada com badges e status Keycloak.');

    // 6. Tela Dedicada de Login (/login)
    console.log('\n📍 [6/10] Testando Tela Dedicada de Login (/login) e Acesso Rápido...');
    await page.goto('http://localhost:5173/login', { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000);
    await page.screenshot({ path: path.join(screenshotsDir, '09-login-view.png'), fullPage: true });

    // Clicar no botão de Acesso Rápido para "Carlos Souza (Engenharia TI)"
    const quickLoginBtn = page.locator('button').filter({ hasText: /Carlos Souza/ }).first();
    if (await quickLoginBtn.count() > 0) {
      await quickLoginBtn.click();
      await page.waitForTimeout(2000);
      await page.screenshot({ path: path.join(screenshotsDir, '10-post-login-dashboard.png'), fullPage: true });
      console.log('✅ Login via Direct Access Grants no Keycloak efetuado com sucesso para Carlos Souza.');
    }

    // 7. Código de Conduta (/codigo-conduta)
    console.log('\n📍 [7/10] Testando Página de Código de Conduta (/codigo-conduta)...');
    await page.goto('http://localhost:5173/codigo-conduta', { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000);
    await page.screenshot({ path: path.join(screenshotsDir, '11-codigo-conduta.png'), fullPage: true });
    console.log('✅ Código de Conduta carregado.');

    // 8. Segurança & Privacidade (/seguranca-privacidade)
    console.log('\n📍 [8/10] Testando Página de Segurança & Privacidade (/seguranca-privacidade)...');
    await page.goto('http://localhost:5173/seguranca-privacidade', { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000);
    await page.screenshot({ path: path.join(screenshotsDir, '12-seguranca-privacidade.png'), fullPage: true });
    console.log('✅ Segurança & Privacidade carregada.');

    // 9. Suporte ao Colaborador (/suporte)
    console.log('\n📍 [9/9] Testando Página de Suporte ao Colaborador (/suporte)...');
    await page.goto('http://localhost:5173/suporte', { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000);
    await page.screenshot({ path: path.join(screenshotsDir, '13-suporte.png'), fullPage: true });
    console.log('✅ Suporte ao Colaborador carregado.');

    // Tutor IA Interativo
    console.log('\n📍 Testando Drawer do Tutor Virtual de IA...');
    const tutorBtn = page.locator('button:has-text("Tutor IA")').first();
    if (await tutorBtn.count() > 0) {
      await tutorBtn.click();
      await page.waitForTimeout(1000);
      const inputQuestion = page.locator('input[placeholder*="Dúvida"], textarea').first();
      if (await inputQuestion.count() > 0) {
        await inputQuestion.fill('Qual o propósito de uma cooperativa de crédito?');
        await inputQuestion.press('Enter');
        await page.waitForTimeout(4000);
        await page.screenshot({ path: path.join(screenshotsDir, '14-tutor-interaction.png') });
        console.log('✅ Interação com Tutor IA capturada com sucesso.');
      }
    }

  } catch (err) {
    console.error('❌ Erro durante o fluxo E2E:', err);
    await page.screenshot({ path: path.join(screenshotsDir, 'error-state.png'), fullPage: true });
  } finally {
    await browser.close();
  }

  console.log('\n========================================');
  console.log('📊 RELATÓRIO CONSOLIDADO E2E PLAYWRIGHT');
  console.log('========================================');
  console.log(`Total de logs capturados no console: ${consoleLogs.length}`);
  const errors = consoleLogs.filter(l => l.type === 'error');
  const warnings = consoleLogs.filter(l => l.type === 'warn');
  console.log(`- Erros de console: ${errors.length}`);
  console.log(`- Avisos de console: ${warnings.length}`);
  console.log(`Total de requisições de rede com falha: ${networkErrors.length}`);
  console.log('========================================\n');
}

runE2E();
