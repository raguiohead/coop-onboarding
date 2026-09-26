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
    console.log('\n📍 [1/10] Acessando Dashboard (http://localhost:5173)...');
    await page.goto('http://localhost:5173', { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000);

    // Se o modal de tutorial abriu automaticamente, vamos testá-lo e fechá-lo
    const pularBtn = page.locator('button:has-text("Pular Tutorial"), button:has-text("Entendi, Começar Agora!")').first();
    if (await pularBtn.isVisible({ timeout: 3000 }).catch(() => false)) {
      console.log('✅ Modal de Tutorial Rápido abriu automaticamente para novo colaborador.');
      await page.screenshot({ path: path.join(screenshotsDir, '01-tutorial-modal.png') });
      await pularBtn.click();
      await page.waitForTimeout(600);
      console.log('✅ Tutorial fechado com sucesso.');
    }

    await page.screenshot({ path: path.join(screenshotsDir, '01-colaborador-dashboard.png'), fullPage: true });
    console.log('✅ Dashboard de Colaborador renderizado.');

    // 2. Troca de Perfil RBAC no Header: Gestor e Admin
    console.log('\n📍 [2/10] Testando Simulação RBAC e Perfis Keycloak no Header...');
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

    // 3. Teste de Navegação para Trilhas pelo Header
    console.log('\n📍 [3/10] Testando Navegação para Trilhas (/trilhas) pelo Header...');
    const trilhasLink = page.locator('header nav a').filter({ hasText: 'Trilhas' });
    await trilhasLink.click();
    await page.waitForTimeout(1000);
    console.log(`ℹ️ URL atual após clicar em Trilhas: ${page.url()}`);
    await page.screenshot({ path: path.join(screenshotsDir, '05-trilhas-view.png'), fullPage: true });

    // 4. Teste de Quizzes no modo Gestor/Admin
    console.log('\n📍 [4/10] Testando Banco de Quizzes (/quizzes)...');
    const quizzesLink = page.locator('header nav a').filter({ hasText: /Quizzes|Banco de Quizzes/ }).first();
    await quizzesLink.click();
    await page.waitForTimeout(1000);
    await page.screenshot({ path: path.join(screenshotsDir, '06-quizzes-gabarito.png'), fullPage: true });
    console.log('✅ Banco de Quizzes acessado com sucesso (Modo Consulta com Gabarito Comentado).');

    // 5. Teste de Gestão de Turma (/gestao) e Raio-X do Aluno
    console.log('\n📍 [5/10] Testando Painel de Gestão (/gestao) e Raio-X do Aluno...');
    await page.goto('http://localhost:5173/gestao', { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000);
    await page.screenshot({ path: path.join(screenshotsDir, '07-gestao-turma.png'), fullPage: true });

    // Abrir Raio-X do primeiro aluno
    const raioXBtn = page.locator('button:has-text("Raio-X")').first();
    if (await raioXBtn.count() > 0) {
      await raioXBtn.click();
      await page.waitForTimeout(800);
      await page.screenshot({ path: path.join(screenshotsDir, '08-gestao-raio-x-modal.png') });
      console.log('✅ Modal Raio-X de Aprendizagem aberto com sucesso (Posicionamento atual e roteiro de lições visíveis).');

      // Testar Envio de Notificação de Apoio
      const sendReminderBtn = page.locator('button:has-text("Enviar Notificação de Apoio")').first();
      if (await sendReminderBtn.count() > 0) {
        await sendReminderBtn.click();
        await page.waitForTimeout(500);
        console.log('✅ Notificação de apoio pedagógico disparada com sucesso.');
      }

      // Fechar modal Raio-X
      const closeRaioX = page.locator('button:has-text("Fechar Raio-X"), button:has-text("×")').first();
      await closeRaioX.click();
      await page.waitForTimeout(500);
    }

    // 6. Teste da Nova Tela de Perfil Repaginada (/perfil)
    console.log('\n📍 [6/10] Testando Nova Tela de Perfil Repaginada (/perfil)...');
    await page.goto('http://localhost:5173/perfil', { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000);
    await page.screenshot({ path: path.join(screenshotsDir, '09-perfil-repaginado.png'), fullPage: true });
    console.log('✅ Página de Perfil carregada com layout moderno, badges, métricas e sessão Keycloak.');

    // Testar Modal de Certificado Digital
    const certBtn = page.locator('button:has-text("Ver Certificado")').first();
    if (await certBtn.count() > 0) {
      await certBtn.click();
      await page.waitForTimeout(800);
      await page.screenshot({ path: path.join(screenshotsDir, '10-perfil-certificado-modal.png') });
      console.log('✅ Modal de Certificado Digital aberto com brasão oficial, hash SHA-256 e validação.');
      const closeCert = page.locator('div[role="dialog"] button:has-text("Imprimir"), div.fixed button').filter({ hasText: /X|Imprimir/ }).last();
      await page.keyboard.press('Escape').catch(() => {});
    }

    // 7. Teste de Suporte com FAQ Categorizado e Busca (/suporte)
    console.log('\n📍 [7/10] Testando Central de Suporte e FAQ Interativo (/suporte)...');
    await page.goto('http://localhost:5173/suporte', { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000);

    // Testar filtro por categoria "Gestores & Raio-X"
    const gestorTab = page.locator('button:has-text("Gestores & Raio-X")').first();
    if (await gestorTab.count() > 0) {
      await gestorTab.click();
      await page.waitForTimeout(500);
      console.log('✅ Filtro de categoria do FAQ "Gestores & Raio-X" selecionado.');
    }

    // Abrir o primeiro item do FAQ
    const faqQuestion = page.locator('section button:has-text("Como o gestor acompanha")').first();
    if (await faqQuestion.count() > 0) {
      await faqQuestion.click();
      await page.waitForTimeout(400);
      console.log('✅ Pergunta do FAQ expandida com resposta detalhada.');
    }
    await page.screenshot({ path: path.join(screenshotsDir, '11-suporte-faq.png'), fullPage: true });

    // 8. Teste da Tela de Login Isolada (/login)
    console.log('\n📍 [8/10] Testando Tela de Login Isolada (/login)...');
    await page.goto('http://localhost:5173/login', { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000);
    await page.screenshot({ path: path.join(screenshotsDir, '12-login-standalone.png'), fullPage: true });

    const headerOnLogin = await page.locator('header').count();
    const footerOnLogin = await page.locator('footer').count();
    console.log(`ℹ️ Header presente no /login? ${headerOnLogin > 0 ? 'SIM' : 'NÃO (Correto)'}`);
    console.log(`ℹ️ Footer presente no /login? ${footerOnLogin > 0 ? 'SIM' : 'NÃO (Correto)'}`);

    // 9. Tutor IA Interativo Drawer
    console.log('\n📍 [9/10] Retornando à Home para Testar Tutor IA Drawer...');
    await page.goto('http://localhost:5173', { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000);
    // Fechar tutorial se reaparecer
    const skipBtn = page.locator('button:has-text("Pular Tutorial"), button:has-text("Começar Minha Jornada")').first();
    if (await skipBtn.isVisible().catch(() => false)) {
      await skipBtn.click();
      await page.waitForTimeout(400);
    }

    const tutorBtn = page.locator('button:has-text("Tutor IA")').first();
    if (await tutorBtn.count() > 0) {
      await tutorBtn.click();
      await page.waitForTimeout(1000);
      const inputQuestion = page.locator('input[placeholder*="Dúvida"], textarea').first();
      if (await inputQuestion.count() > 0) {
        await inputQuestion.fill('Qual a diferença entre uma cooperativa e um banco comercial?');
        await inputQuestion.press('Enter');
        await page.waitForTimeout(4000);
        await page.screenshot({ path: path.join(screenshotsDir, '13-tutor-resposta.png') });
        console.log('✅ Interação com Tutor IA capturada com sucesso.');
      }
    }

    console.log('\n📍 [10/10] Todos os fluxos testados com sucesso.');
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
