import type { AskTutorResponse, GenerateQuizResponse, Track } from '@/types';

const BASE_URL = '/api/v1';

class ApiClient {
  private token: string | null = null;
  private refreshTokenHandler: (() => Promise<string | null>) | null = null;
  private isRefreshing = false;

  setToken(token: string | null) {
    this.token = token;
  }

  setRefreshTokenHandler(handler: () => Promise<string | null>) {
    this.refreshTokenHandler = handler;
  }

  private getHeaders(): HeadersInit {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };
    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }
    return headers;
  }

  private async fetchWithRetry(url: string, init: RequestInit = {}): Promise<Response> {
    const headers = {
      ...this.getHeaders(),
      ...(init.headers as Record<string, string> || {}),
    };

    let res = await fetch(url, { ...init, headers });

    // Se receber 401 e tivermos handler de refresh, tenta renovar o token e retentar uma vez
    if (res.status === 401 && this.refreshTokenHandler && !this.isRefreshing) {
      this.isRefreshing = true;
      try {
        const newToken = await this.refreshTokenHandler();
        if (newToken) {
          const retryHeaders = {
            ...headers,
            'Authorization': `Bearer ${newToken}`,
          };
          res = await fetch(url, { ...init, headers: retryHeaders });
        }
      } catch (err) {
        console.warn('Falha no auto-refresh do token Keycloak:', err);
      } finally {
        this.isRefreshing = false;
      }
    }

    return res;
  }

  async getTracks(): Promise<Track[]> {
    const res = await this.fetchWithRetry(`${BASE_URL}/tracks`);
    if (!res.ok) {
      throw new Error(`Falha ao buscar trilhas: ${res.statusText}`);
    }
    return res.json();
  }

  async askTutor(lessonId: string, question: string): Promise<AskTutorResponse> {
    const res = await this.fetchWithRetry(`${BASE_URL}/ai/tutor/ask`, {
      method: 'POST',
      body: JSON.stringify({ lessonId, question }),
    });
    if (!res.ok) {
      throw new Error(`Falha ao consultar tutor: ${res.statusText}`);
    }
    return res.json();
  }

  async streamTutor(
    lessonId: string,
    question: string,
    onChunk: (chunk: string) => void
  ): Promise<string> {
    const params = new URLSearchParams({ lessonId, question });
    const res = await this.fetchWithRetry(`${BASE_URL}/ai/tutor/stream?${params.toString()}`, {
      method: 'GET',
      headers: {
        'Accept': 'text/event-stream',
      },
    });

    if (!res.ok || !res.body) {
      throw new Error(`Falha no streaming do tutor: ${res.statusText}`);
    }

    const reader = res.body.getReader();
    const decoder = new TextDecoder('utf-8');
    let fullText = '';
    let buffer = '';

    try {
      while (true) {
        const { done, value } = await reader.read();
        if (done) break;
        buffer += decoder.decode(value, { stream: true });
        
        const lines = buffer.split('\n');
        // Mantém a última linha incompleta no buffer
        buffer = lines.pop() ?? '';

        for (const line of lines) {
          if (line.startsWith('data:')) {
            // No Spring WebFlux, o chunk bruto vem imediatamente após 'data:'
            const rawChunk = line.substring(5);
            if (rawChunk) {
              fullText += rawChunk;
              onChunk(rawChunk);
            }
          }
        }
      }

      // Processa qualquer resíduo no buffer final
      if (buffer.startsWith('data:')) {
        const rawChunk = buffer.substring(5);
        if (rawChunk) {
          fullText += rawChunk;
          onChunk(rawChunk);
        }
      }
    } finally {
      reader.releaseLock();
    }

    return fullText;
  }

  async generateQuiz(lessonId: string, contentMarkdown?: string, questionCount: number = 3): Promise<GenerateQuizResponse> {
    const res = await this.fetchWithRetry(`${BASE_URL}/ai/quiz/generate`, {
      method: 'POST',
      body: JSON.stringify({ lessonId, contentMarkdown, questionCount }),
    });
    if (!res.ok) {
      throw new Error(`Falha ao gerar quiz: ${res.statusText}`);
    }
    return res.json();
  }
}

export const api = new ApiClient();
