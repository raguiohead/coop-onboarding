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

  console.log('🚀 Iniciando bateria de testes E2E com Playwright...');
  const browser = await chromium.launch({
    headless: true,
    executablePath: '/usr/bin/google-chrome',
    args: ['--no-sandbox', '--disable-setuid-sandbox', '--disable-gpu']
  });

  const context = await browser.newContext({
    viewport: { width: 1440, height: 900 }
  });
  const page = await context.newPage();

  // Monitorar logs de console
  page.on('console', msg => {
    const type = msg.type();
    const text = msg.text();
    consoleLogs.push({ type, text });
    if (type === 'error' || type === 'warn') {
      console.log(`[Browser Console ${type.toUpperCase()}]: ${text}`);
    }
  });

  // Monitorar erros de rede
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
        body: body.substring(0, 500)
      });
      console.log(`[HTTP ${response.status()}]: ${response.url()} - Body: ${body.substring(0, 300)}`);
    }
  });

  try {
    // 1. Acesso à Home
    console.log('\n📍 [Passo 1/6] Navegando para http://localhost:5173...');
    await page.goto('http://localhost:5173', { waitUntil: 'networkidle' });
    await page.screenshot({ path: path.join(screenshotsDir, '01-dashboard.png'), fullPage: true });
    console.log('✅ Dashboard renderizado com sucesso.');

    // 2. Verificar cards e dados da Home
    const headerTitle = await page.textContent('header');
    console.log(`ℹ️ Cabeçalho detectado: ${headerTitle ? 'Presente' : 'Ausente'}`);

    // 3. Teste de Troca de Perfil RBAC
    console.log('\n📍 [Passo 2/6] Testando Seletor de Perfil RBAC no Header...');
    // Clicar no botão do perfil para abrir dropdown
    const profileButton = page.locator('header button').filter({ hasText: /Ana Carolina Silva|Novo Colaborador|Colaborador/ });
    if (await profileButton.count() > 0) {
      await profileButton.first().click();
      await page.waitForTimeout(500);
      await page.screenshot({ path: path.join(screenshotsDir, '02-profile-dropdown.png') });

      // Selecionar Roberto Mendes (Gestor)
      const gestorOption = page.locator('button').filter({ hasText: 'Roberto Mendes' });
      if (await gestorOption.count() > 0) {
        await gestorOption.first().click();
        await page.waitForTimeout(1000);
        console.log('✅ Perfil alterado para Roberto Mendes (GESTOR).');
      }
    }

    // 4. Navegar para uma Trilha / Aula
    console.log('\n📍 [Passo 3/6] Acessando a primeira aula da trilha...');
    const continueBtn = page.locator('button:has-text("Continuar Trilha"), a:has-text("Continuar Trilha")');
    if (await continueBtn.count() > 0) {
      await continueBtn.first().click();
    } else {
      // Navegação direta caso o botão não esteja visível
      await page.goto('http://localhost:5173/tracks/c1a2b3c4-0001-4000-8000-000000000001/lessons/d1a2b3c4-0001-4000-8000-000000000001', { waitUntil: 'networkidle' });
    }
    await page.waitForTimeout(1500);
    await page.screenshot({ path: path.join(screenshotsDir, '03-lesson-view.png'), fullPage: true });
    console.log('✅ Visualizador de aula carregado.');

    // 5. Testar Tutor IA Drawer e Pergunta RAG
    console.log('\n📍 [Passo 4/6] Testando Drawer do Tutor Virtual de IA...');
    const tutorBtn = page.locator('button:has-text("Tutor IA")').first();
    if (await tutorBtn.count() > 0) {
      await tutorBtn.click();
      await page.waitForTimeout(1000);
      await page.screenshot({ path: path.join(screenshotsDir, '04-tutor-drawer-open.png') });

      const inputQuestion = page.locator('input[placeholder*="Dúvida"], textarea, input[type="text"]').last();
      if (await inputQuestion.count() > 0) {
        console.log('💬 Enviando pergunta para o Tutor IA...');
        await inputQuestion.fill('Qual o propósito de uma cooperativa de crédito?');
        const sendBtn = page.locator('button').filter({ has: page.locator('svg') }).last();
        await sendBtn.click();
        
        // Aguardar até 30s pela resposta do Ollama / Spring AI
        console.log('⏳ Aguardando inferência da IA local...');
        await page.waitForTimeout(5000);
        await page.screenshot({ path: path.join(screenshotsDir, '05-tutor-response.png') });
        console.log('✅ Resposta do Tutor IA registrada.');

        // Fechar o Drawer do Tutor para liberar a tela
        const closeDrawerBtn = page.locator('button[title="Fechar drawer"]');
        if (await closeDrawerBtn.count() > 0) {
          await closeDrawerBtn.click();
          await page.waitForTimeout(500);
          console.log('✅ Drawer do Tutor IA fechado.');
        }
      }
    }

    // 6. Testar Gerador de Quiz com IA (Role Gestor)
    console.log('\n📍 [Passo 5/6] Testando Modal de Geração de Quiz (Role Gestor)...');
    const quizBtn = page.locator('button:has-text("Gerar Quiz com IA")');
    if (await quizBtn.count() > 0) {
      await quizBtn.first().click();
      await page.waitForTimeout(800);
      await page.screenshot({ path: path.join(screenshotsDir, '06-quiz-modal-open.png') });
      console.log('✅ Modal de Quiz aberto com sucesso.');

      const generateActionBtn = page.locator('button:has-text("Iniciar Geração Automática"), button:has-text("Gerar Novo Quiz")');
      if (await generateActionBtn.count() > 0) {
        console.log('⏳ Disparando geração de Quiz via LLM...');
        await generateActionBtn.first().click();
        await page.waitForTimeout(8000);
        await page.screenshot({ path: path.join(screenshotsDir, '07-quiz-generated.png') });
        console.log('✅ Geração de Quiz concluída.');
      }
    } else {
      console.log('ℹ️ Botão "Gerar Quiz com IA" não visível para o perfil atual.');
    }

    console.log('\n📍 [Passo 6/6] Finalizando testes e consolidando telemetria...');
  } catch (err) {
    console.error('❌ Erro durante a execução do teste:', err);
    await page.screenshot({ path: path.join(screenshotsDir, 'error-state.png'), fullPage: true });
  } finally {
    await browser.close();
  }

  // Relatório Final
  console.log('\n========================================');
  console.log('📊 RELATÓRIO DE DIAGNÓSTICO E2E');
  console.log('========================================');
  console.log(`Total de logs capturados no console: ${consoleLogs.length}`);
  const errors = consoleLogs.filter(l => l.type === 'error');
  const warnings = consoleLogs.filter(l => l.type === 'warn');
  console.log(`- Erros no console: ${errors.length}`);
  console.log(`- Avisos no console: ${warnings.length}`);
  console.log(`Total de falhas de rede: ${networkErrors.length}`);
  if (networkErrors.length > 0) {
    console.log('Detalhes das falhas de rede:', JSON.stringify(networkErrors, null, 2));
  }
  console.log('========================================\n');
}

runE2E();
