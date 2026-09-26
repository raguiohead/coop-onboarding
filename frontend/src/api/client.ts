import type { AskTutorResponse, GenerateQuizResponse, Track } from '@/types';

const BASE_URL = '/api/v1';

class ApiClient {
  private token: string | null = null;

  setToken(token: string | null) {
    this.token = token;
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

  async getTracks(): Promise<Track[]> {
    const res = await fetch(`${BASE_URL}/tracks`, {
      headers: this.getHeaders(),
    });
    if (!res.ok) {
      throw new Error(`Falha ao buscar trilhas: ${res.statusText}`);
    }
    return res.json();
  }

  async askTutor(lessonId: string, question: string): Promise<AskTutorResponse> {
    const res = await fetch(`${BASE_URL}/ai/tutor/ask`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ lessonId, question }),
    });
    if (!res.ok) {
      throw new Error(`Falha ao consultar tutor: ${res.statusText}`);
    }
    return res.json();
  }

  async generateQuiz(lessonId: string, contentMarkdown?: string, questionCount: number = 3): Promise<GenerateQuizResponse> {
    const res = await fetch(`${BASE_URL}/ai/quiz/generate`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ lessonId, contentMarkdown, questionCount }),
    });
    if (!res.ok) {
      throw new Error(`Falha ao gerar quiz: ${res.statusText}`);
    }
    return res.json();
  }
}

export const api = new ApiClient();
