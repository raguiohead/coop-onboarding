import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { UserProfile, UserRole } from '@/types';
import { api } from '@/api/client';

export const mockProfiles: UserProfile[] = [
  {
    id: '11111111-1111-1111-1111-111111111111',
    name: 'Ana Carolina Silva',
    email: 'ana.silva@cooperativa.com.br',
    role: 'COLABORADOR',
    department: 'Atendimento & Crédito',
    avatarUrl: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80',
    joinDate: '15/09/2026',
  },
  {
    id: '22222222-2222-2222-2222-222222222222',
    name: 'Roberto Mendes',
    email: 'roberto.mendes@cooperativa.com.br',
    role: 'GESTOR',
    department: 'Gente & Gestão',
    avatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80',
    joinDate: '10/01/2024',
  },
  {
    id: '33333333-3333-3333-3333-333333333333',
    name: 'Mariana Duarte',
    email: 'mariana.duarte@cooperativa.com.br',
    role: 'ADMIN',
    department: 'Tecnologia & Governança',
    avatarUrl: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150&auto=format&fit=crop&q=80',
    joinDate: '01/03/2023',
  },
];

export const useAuthStore = defineStore('auth', () => {
  const currentUser = ref<UserProfile>(mockProfiles[0]);
  const token = ref<string | null>(localStorage.getItem('coop_auth_token'));

  if (token.value) {
    api.setToken(token.value);
  }

  const isColaborador = computed(() => currentUser.value.role === 'COLABORADOR');
  const isGestor = computed(() => currentUser.value.role === 'GESTOR');
  const isAdmin = computed(() => currentUser.value.role === 'ADMIN');
  const canGenerateQuiz = computed(() => currentUser.value.role === 'GESTOR' || currentUser.value.role === 'ADMIN');

  const roleBadge = computed(() => {
    switch (currentUser.value.role) {
      case 'ADMIN':
        return { label: 'Administrador', bg: 'bg-rose-100 text-rose-800 border-rose-200' };
      case 'GESTOR':
        return { label: 'Gestor de Trilha', bg: 'bg-indigo-100 text-indigo-800 border-indigo-200' };
      default:
        return { label: 'Novo Colaborador', bg: 'bg-teal-100 text-teal-800 border-teal-200' };
    }
  });

  function switchProfile(profileId: string) {
    const found = mockProfiles.find((p) => p.id === profileId);
    if (found) {
      currentUser.value = { ...found };
    }
  }

  function setRole(role: UserRole) {
    currentUser.value.role = role;
  }

  function setToken(newToken: string | null) {
    token.value = newToken;
    if (newToken) {
      localStorage.setItem('coop_auth_token', newToken);
      api.setToken(newToken);
    } else {
      localStorage.removeItem('coop_auth_token');
      api.setToken(null);
    }
  }

  return {
    currentUser,
    token,
    mockProfiles,
    isColaborador,
    isGestor,
    isAdmin,
    canGenerateQuiz,
    roleBadge,
    switchProfile,
    setRole,
    setToken,
  };
});
