package com.coop.onboarding.infrastructure.ai.config;

import com.coop.onboarding.application.ai.IngestLessonCommand;
import com.coop.onboarding.application.ai.IngestLessonUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
@Order(5)
public class KnowledgeBaseSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseSeeder.class);

    private final IngestLessonUseCase ingestLessonUseCase;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public KnowledgeBaseSeeder(IngestLessonUseCase ingestLessonUseCase, JdbcTemplate jdbcTemplate) {
        this.ingestLessonUseCase = ingestLessonUseCase;
        this.jdbcTemplate = jdbcTemplate;
    }

    private record LessonSeed(String rawId, String title, String markdown) {}

    @Override
    public void run(ApplicationArguments args) {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM vector_store", Integer.class);
            if (count != null && count >= 7) {
                log.info("Base vetorial já indexada com {} chunks de conhecimento. Pulando seed.", count);
                return;
            }

            log.info("Indexando acervo de lições no VectorStore (pgvector)...");

            List<LessonSeed> seeds = List.of(
                    new LessonSeed(
                            "les-101-1",
                            "Os 7 Princípios do Cooperativismo no Cotidiano",
                            """
                            # Os 7 Princípios do Cooperativismo no Cotidiano
                            
                            Como cooperativa de crédito, nossa razão de existir não é maximizar o lucro de acionistas, mas promover a prosperidade econômica e social de nossos associados e da comunidade.
                            
                            1. Adesão Voluntária e Livre: Cooperativas são abertas a todos aptos a utilizar seus serviços, sem discriminação social, racial, política, religiosa ou de gênero.
                            2. Gestão Democrática pelos Membros: "Uma pessoa, um voto". Na cooperativa cada associado possui igual poder de voto nas Assembleias Gerais (AGO e AGE), independentemente do seu patrimônio ou cota capital.
                            3. Participação Econômica dos Membros: Os resultados positivos gerados são chamados de Sobras líquidas. Elas retornam aos associados proporcionalmente à sua movimentação financeira anual ou são reinvestidas por decisão assemblear.
                            4. Autonomia e Independência: Organizações autônomas de ajuda mútua geridas democraticamente por seus próprios membros.
                            5. Educação, Formação e Informação: Desenvolvimento profissional constante dos colaboradores e educação financeira aos cooperados.
                            6. Intercooperação: Cooperação entre cooperativas de diversos ramos econômicos (crédito, agro, saúde) construindo redes resilientes.
                            7. Interesse pela Comunidade: Destinação obrigatória de recursos ao Fundo de Assistência Técnica, Educacional e Social (FATES) e projetos de desenvolvimento sustentável da região.
                            """
                    ),
                    new LessonSeed(
                            "les-101-2",
                            "Estrutura de Governança, Conselhos e Assembleias",
                            """
                            # Estrutura de Governança e Tomada de Decisão
                            
                            A governança cooperativa é sustentada por três pilares essenciais: transparência, prestação de contas (accountability) e conformidade regulatória perante o Banco Central do Brasil (BACEN).
                            
                            1. Assembleia Geral: Instância soberana máxima onde cooperados deliberam sobre prestação de contas, balanços, destinação de sobras líquidas e eleição de conselhos.
                            2. Conselho de Administração: Eleito pelos cooperados para fixar diretrizes estratégicas, políticas de crédito e supervisionar a Diretoria Executiva.
                            3. Diretoria Executiva: Gestores dedicados à condução operacional diária, alcance de metas e cumprimento normativo.
                            4. Conselho Fiscal: Fiscalização independente, contínua e autônoma dos livros contábeis, finanças e atos de gestão.
                            """
                    ),
                    new LessonSeed(
                            "les-102-1",
                            "Código de Conduta e Ética Cooperativista",
                            """
                            # Código de Conduta e Ética Cooperativista
                            
                            Nossa reputação é o maior patrimônio. O Código de Conduta se aplica a todos os colaboradores e conselheiros.
                            
                            1. Confidencialidade Estrita: Informações cadastrais e financeiras de associados jamais devem ser expostas fora dos sistemas corporativos.
                            2. Prevenção a Conflito de Interesses: Vedado tratamento privilegiado a parentes ou parceiros comerciais em propostas de crédito.
                            3. Política de Brindes: Permitidos apenas presentes de valor simbólico institucional de até R$ 100,00.
                            4. Canal de Denúncias Independente: Canal 100% anônimo e protegido contra retaliações para reporte de condutas irregulares ou assédio.
                            """
                    ),
                    new LessonSeed(
                            "les-201-1",
                            "Tratamento de Dados e Bases Legais na LGPD",
                            """
                            # LGPD no Ambiente Cooperativo de Crédito
                            
                            A Lei Geral de Proteção de Dados (Lei nº 13.709/2018) estabelece diretrizes rigorosas para o tratamento de dados pessoais no setor financeiro.
                            Bases Legais no Cooperativismo:
                            - Execução de Contrato: Coleta de dados necessária para abertura de conta corrente, empréstimos e cartões.
                            - Cumprimento de Obrigação Legal/Regulatória: Diretrizes do BACEN, Receita Federal e COAF.
                            - Proteção do Crédito: Consultas aos birôs e bureau SCR/BACEN.
                            Boas Práticas: Sempre bloquear a tela ao ausentar-se (Super + L / Win + L), não trafegar dados de cooperados por canais pessoais e descartar impressões em fragmentadoras de segurança.
                            """
                    ),
                    new LessonSeed(
                            "les-201-2",
                            "Prevenção à Lavagem de Dinheiro (PLD-FT)",
                            """
                            # Prevenção à Lavagem de Dinheiro e Financiamento ao Terrorismo (PLD-FT)
                            
                            Em conformidade com a Circular BACEN nº 3.978/2020 e a Lei nº 9.613/1998, as cooperativas de crédito blindam o Sistema Financeiro Nacional.
                            As Três Fases da Lavagem de Dinheiro:
                            1. Colocação (Placement): Inserção de recursos ilícitos no sistema bancário via depósitos fracionados em espécie.
                            2. Ocultação (Layering): Estruturação de múltiplas transferências para mascarar a titularidade e origem dos fundos.
                            3. Integração (Integration): Reintrodução do capital dissimulado na economia formal através de ativos legítimos.
                            Obrigações do Colaborador: Procedimentos KYC (Conheça Seu Cliente), identificação de PEP (Pessoas Expostas Politicamente) e comunicação compulsória ao COAF de operações suspeitas.
                            """
                    ),
                    new LessonSeed(
                            "les-301-1",
                            "Admissão de Cooperados e Integralização da Cota Capital",
                            """
                            # Admissão de Cooperado e Integralização da Cota Capital
                            
                            Diferente de um cliente de banco comum, quem ingressa em uma cooperativa se torna dono do negócio através da subscrição de sua cota capital.
                            O que é Cota Capital:
                            - Fração de participação societária no patrimônio líquido da cooperativa;
                            - Garante o direito a voto nas Assembleias;
                            - Recebe remuneração anual por juros ao capital (limitados à taxa Selic);
                            - Resgatável conforme as normas e prazos do Estatuto Social.
                            Etapas de admissão: validação de documentos de renda e residência, checagem de restrições cadastrais, integralização da cota inicial e assinatura eletrônica do termo de associação.
                            """
                    ),
                    new LessonSeed(
                            "d1a2b3c4-0001-4000-8000-000000000001",
                            "Visão Geral de Formação e Princípios Cooperativistas",
                            """
                            # Formação Integrada e Onboarding Cooperativo
                            
                            Resumo geral do programa de integração:
                            - Fundamentos do Cooperativismo de Crédito e Governança Democrática;
                            - Diferença essencial entre Sobras cooperativas e Lucro de bancos mercantis;
                            - Segurança da Informação, Sigilo Bancário e LGPD;
                            - Procedimentos normativos de combate à lavagem de dinheiro (PLD-FT);
                            - Acesso ao Tutor Virtual com inteligência artificial para apoio pedagógico contínuo aos novos colaboradores.
                            """
                    )
            );

            for (LessonSeed seed : seeds) {
                UUID lessonUuid;
                try {
                    lessonUuid = UUID.fromString(seed.rawId());
                } catch (IllegalArgumentException e) {
                    lessonUuid = UUID.nameUUIDFromBytes(seed.rawId().getBytes(StandardCharsets.UTF_8));
                }

                ingestLessonUseCase.execute(new IngestLessonCommand(
                        lessonUuid,
                        seed.title(),
                        seed.markdown(),
                        null,
                        null
                ));
            }

            log.info("Todas as {} aulas do currículo foram vetorizadas no pgvector com sucesso!", seeds.size());
        } catch (Exception e) {
            log.error("Erro durante o seed da base de conhecimento: {}", e.getMessage(), e);
        }
    }
}
