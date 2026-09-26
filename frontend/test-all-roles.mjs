import { chromium } from 'playwright';
import fs from 'node:fs';

const screenshotDir = './test-screenshots';
if (!fs.existsSync(screenshotDir)) {
  fs.mkdirSync(screenshotDir, { recursive: true });
}

async function runTests() {
  console.log('🚀 Iniciando bateria de testes E2E com Playwright...');
  const browser = await chromium.launch({
    headless: true,
    executablePath: '/usr/bin/google-chrome-stable',
    args: ['--no-sandbox', '--disable-setuid-sandbox', '--disable-dev-shm-usage']
  });

  const context = await browser.newContext({
    viewport: { width: 1600, height: 950 }
  });
  const page = await context.newPage();

  const results = {
    colaborador: { ok: false, issues: [] },
    gestor: { ok: false, issues: [] },
    admin: { ok: false, issues: [] },
    tutorAi: { ok: false, issues: [] },
  };

  try {
    // ==========================================
    // 1. TESTE COLABORADOR
    // ==========================================
    console.log('\n--- [1] Testando Perfil COLABORADOR ---');
    await page.goto('http://localhost:5173/login');
    await page.waitForLoadState('networkidle');

    // Preenche login
    await page.fill('input[type="text"]', 'colaborador');
    await page.fill('input[type="password"]', 'colab123');
    await page.click('button[type="submit"]');
    await page.waitForURL('http://localhost:5173/', { timeout: 8000 });
    console.log('✅ Login como colaborador bem-sucedido.');

    await page.waitForTimeout(1000);
    await page.screenshot({ path: `${screenshotDir}/01-colaborador-home.png` });

    // O modal abre automaticamente no primeiro login do colaborador
    await page.waitForTimeout(1000);
    let modalHeader = await page.$('text=Guia Rápido de Onboarding');
    if (modalHeader) {
      console.log('✅ Modal do Guia Rápido abriu automaticamente e centralizado.');
      await page.screenshot({ path: `${screenshotDir}/02-colaborador-guia-rapido.png` });
      const closeBtn = await page.$('button[title="Fechar tutorial"]') || await page.$('text=Pular Tutorial');
      if (closeBtn) await closeBtn.click();
      await page.waitForTimeout(500);
    }

    // Verifica que botão Guia Rápido existe no cabeçalho para colaborador
    const guiaRapidoBtn = await page.$('text=Guia Rápido');
    if (!guiaRapidoBtn) {
      results.colaborador.issues.push('Botão Guia Rápido não encontrado para colaborador.');
    } else {
      console.log('✅ Botão Guia Rápido visível no cabeçalho para Colaborador.');
    }

    // Testa Tutor IA
    console.log('\n--- Testando Tutor IA ---');
    const tutorBtn = await page.$('button[title*="Tutor Virtual"]');
    if (!tutorBtn) {
      results.tutorAi.issues.push('Botão flutuante do Tutor IA não encontrado na tela.');
    } else {
      console.log('✅ Botão flutuante do Tutor IA localizado.');
      await tutorBtn.click();
      await page.waitForTimeout(600);
      await page.screenshot({ path: `${screenshotDir}/03-tutor-ia-drawer.png` });

      // Envia uma pergunta
      const inputSelector = 'textarea, input[placeholder*="Dúvida"], input[placeholder*="Pergunte"]';
      const input = await page.$(inputSelector);
      if (input) {
        await input.fill('O que sao as sobras cooperativas?');
        await page.keyboard.press('Enter');
        console.log('⏳ Pergunta enviada ao Tutor IA. Aguardando inferência LLM (Ollama)...');

        // Aguarda resposta do tutor (até 25 segundos para LLM local na CPU)
        await page.waitForTimeout(12000);
        await page.screenshot({ path: `${screenshotDir}/04-tutor-ia-resposta.png` });

        // Verifica se há resposta do tutor
        const messageElements = await page.$$('.prose, p');
        let answered = false;
        for (const el of messageElements) {
          const txt = await el.innerText();
          if (txt.toLowerCase().includes('sobra') || txt.toLowerCase().includes('cooperativ')) {
            answered = true;
            console.log('✅ Resposta gerada pelo Tutor IA verificada com sucesso:', txt.substring(0, 100) + '...');
            break;
          }
        }

        if (answered) {
          results.tutorAi.ok = true;
        } else {
          results.tutorAi.issues.push('Tutor IA não respondeu ou não exibiu texto contendo termos da aula.');
        }

        // Fecha o drawer
        const closeDrawerBtn = await page.$('button[title="Fechar drawer"]');
        if (closeDrawerBtn) await closeDrawerBtn.click();
        await page.waitForTimeout(400);
      }
    }

    // Testa RBAC: Colaborador não pode acessar /gestao
    console.log('\n--- Testando Proteção de Rota (/gestao) para Colaborador ---');
    await page.goto('http://localhost:5173/gestao');
    await page.waitForTimeout(500);
    const currentUrl = page.url();
    if (currentUrl === 'http://localhost:5173/' || currentUrl === 'http://localhost:5173') {
      console.log('✅ RBAC aprovado: Colaborador foi impedido de acessar /gestao e redirecionado para /.');
      results.colaborador.ok = true;
    } else {
      results.colaborador.issues.push(`RBAC falhou: Colaborador acessou rota restrita ${currentUrl}`);
    }

    // Logout
    await page.click('button:has(img[alt="Ana Carolina Silva"])');
    await page.waitForTimeout(300);
    await page.click('text=Encerrar Sessão');
    await page.waitForURL('http://localhost:5173/login');
    console.log('✅ Logout concluído com sucesso.');

    // ==========================================
    // 2. TESTE GESTOR
    // ==========================================
    console.log('\n--- [2] Testando Perfil GESTOR (roberto.mendes) ---');
    await page.fill('input[type="text"]', 'gestor');
    await page.fill('input[type="password"]', 'gestor123');
    await page.click('button[type="submit"]');
    await page.waitForURL('http://localhost:5173/', { timeout: 8000 });
    await page.waitForTimeout(800);
    await page.screenshot({ path: `${screenshotDir}/05-gestor-home.png` });

    // Verifica que Guia Rápido NÃO existe para Gestor
    const guiaRapidoGestor = await page.$('text=Guia Rápido');
    if (guiaRapidoGestor) {
      results.gestor.issues.push('ERRO: Guia Rápido ainda está visível para o Gestor!');
    } else {
      console.log('✅ Confirmado: Guia Rápido está oculto para Gestor.');
    }

    // Navega para /gestao
    await page.goto('http://localhost:5173/gestao');
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(800);
    await page.screenshot({ path: `${screenshotDir}/06-gestor-gestao.png` });

    // Testa Raio-X
    const raioXBtn = await page.$('button:has-text("Raio-X")');
    if (raioXBtn) {
      await raioXBtn.click();
      await page.waitForTimeout(500);
      const closeRaioX = await page.$('button:has-text("Fechar Raio-X")');
      if (closeRaioX) {
        console.log('✅ Modal de Raio-X abriu com sucesso para o Gestor.');
        await page.screenshot({ path: `${screenshotDir}/07-gestor-raio-x.png` });
        await closeRaioX.click();
        await page.waitForTimeout(500);
        console.log('✅ Modal de Raio-X fechado com sucesso.');
      } else {
        await page.keyboard.press('Escape');
        await page.waitForTimeout(500);
      }
    }

    // Gestor não deve ter botões de Admin CRUD (Novo Membro)
    const novoMembroBtn = await page.$('text=Novo Membro');
    if (novoMembroBtn) {
      results.gestor.issues.push('ERRO: Botão Novo Membro (Admin) apareceu para Gestor!');
    } else {
      console.log('✅ Confirmado: Gestor não possui botões administrativos (Novo Membro).');
      results.gestor.ok = true;
    }

    // Logout Gestor
    await page.click('button:has(img[alt*="Roberto"])');
    await page.waitForTimeout(300);
    await page.click('text=Encerrar Sessão');
    await page.waitForURL('http://localhost:5173/login');

    // ==========================================
    // 3. TESTE ADMIN
    // ==========================================
    console.log('\n--- [3] Testando Perfil ADMIN (mariana.duarte) ---');
    await page.fill('input[type="text"]', 'admin');
    await page.fill('input[type="password"]', 'admin123');
    await page.click('button[type="submit"]');
    await page.waitForURL('http://localhost:5173/', { timeout: 8000 });
    await page.waitForTimeout(800);
    await page.screenshot({ path: `${screenshotDir}/08-admin-home.png` });

    // Verifica que Guia Rápido NÃO existe para Admin
    const guiaRapidoAdmin = await page.$('text=Guia Rápido');
    if (guiaRapidoAdmin) {
      results.admin.issues.push('ERRO: Guia Rápido ainda está visível para o Admin!');
    } else {
      console.log('✅ Confirmado: Guia Rápido está oculto para Admin.');
    }

    // Verifica os 4 Cards Enterprise
    const enterpriseCard = await page.$('text=Serviços Enterprise');
    const pgvectorCard = await page.$('text=Base Vetorial pgvector');
    const keycloakCard = await page.$('text=Segurança IAM Keycloak');
    const llmCard = await page.$('text=Inferência LLM Local');

    if (enterpriseCard && pgvectorCard && keycloakCard && llmCard) {
      console.log('✅ Todos os 4 Cards Enterprise estão visíveis para o Administrador.');
    } else {
      results.admin.issues.push('Nem todos os 4 cards de governança do Admin foram renderizados.');
    }

    // Navega para /gestao e verifica CRUD Admin
    await page.goto('http://localhost:5173/gestao');
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(800);
    await page.screenshot({ path: `${screenshotDir}/09-admin-gestao.png` });

    const adminNovoMembro = await page.$('text=Novo Membro');
    if (adminNovoMembro) {
      console.log('✅ Botão "Novo Membro" exibido com sucesso para Admin.');
      results.admin.ok = true;
    } else {
      results.admin.issues.push('Botão Novo Membro não foi encontrado para Admin em /gestao');
    }

    // Verifica /trilhas no modo consulta
    await page.goto('http://localhost:5173/trilhas');
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(600);
    await page.screenshot({ path: `${screenshotDir}/10-admin-trilhas-consulta.png` });

    const bannerAdmin = await page.$('text=Visão de Governança & Consulta');
    if (bannerAdmin) {
      console.log('✅ Página de Trilhas exibida em modo consulta para o Admin.');
    }

  } catch (err) {
    console.error('❌ Erro inesperado durante execução dos testes:', err);
  } finally {
    await browser.close();
    console.log('\n==========================================');
    console.log('📊 RESULTADO CONSOLIDADO DOS TESTES:');
    console.log('==========================================');
    console.log('Colaborador:', results.colaborador.ok ? '✅ APROVADO' : '❌ FALHOU', results.colaborador.issues);
    console.log('Gestor:     ', results.gestor.ok ? '✅ APROVADO' : '❌ FALHOU', results.gestor.issues);
    console.log('Admin:      ', results.admin.ok ? '✅ APROVADO' : '❌ FALHOU', results.admin.issues);
    console.log('Tutor IA:   ', results.tutorAi.ok ? '✅ APROVADO' : '❌ FALHOU', results.tutorAi.issues);
    console.log('==========================================\n');
  }
}

runTests();
