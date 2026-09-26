export type UserRole = 'COLABORADOR' | 'GESTOR' | 'ADMIN';

export interface UserProfile {
  id: string;
  name: string;
  email: string;
  role: UserRole;
  department: string;
  avatarUrl?: string;
  joinDate?: string;
}

export interface Lesson {
  id: string;
  moduleId: string;
  title: string;
  orderIndex: number;
  estimatedMinutes: number;
  contentMarkdown: string;
  completed?: boolean;
}

export interface Module {
  id: string;
  trackId: string;
  title: string;
  description: string;
  orderIndex: number;
  lessons: Lesson[];
}

export interface Track {
  id: string;
  title: string;
  description: string;
  targetDepartment: string;
  estimatedHours: number;
  isActive: boolean;
  slaDays: number;
  modules: Module[];
  progressPercent?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface AiMessage {
  id: string;
  sender: 'user' | 'tutor';
  text: string;
  timestamp: string;
  sources?: string[];
  isLoading?: boolean;
}

export interface QuizQuestion {
  question: string;
  options: string[];
  correctAnswer: string;
  explanation: string;
}

export interface GeneratedQuiz {
  lessonId: string;
  questions: QuizQuestion[];
}

export interface AskTutorRequest {
  lessonId: string;
  question: string;
}

export interface AskTutorResponse {
  lessonId: string;
  answer: string;
  sources: string[];
}

export interface GenerateQuizRequest {
  lessonId: string;
  contentMarkdown?: string;
  questionCount?: number;
}

export interface GenerateQuizResponse {
  lessonId: string;
  questions: QuizQuestion[];
}
