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

const profileCredentials: Record<string, { user: string; pass: string }> = {
  '11111111-1111-1111-1111-111111111111': { user: 'colaborador', pass: 'colab123' },
  '22222222-2222-2222-2222-222222222222': { user: 'gestor', pass: 'gestor123' },
  '33333333-3333-3333-3333-333333333333': { user: 'admin', pass: 'admin123' },
};

export const useAuthStore = defineStore('auth', () => {
  const currentUser = ref<UserProfile>(mockProfiles[0]);
  const token = ref<string | null>(localStorage.getItem('coop_auth_token'));
  const refreshToken = ref<string | null>(localStorage.getItem('coop_refresh_token'));

  if (token.value) {
    api.setToken(token.value);
  }

  // Registra o interceptor de renovação automática no cliente HTTP
  api.setRefreshTokenHandler(refreshKeycloakToken);

  // Tenta sincronizar com o Keycloak na inicialização se não houver token ou para renovar
  syncKeycloakToken(currentUser.value.id);

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

  async function syncKeycloakToken(profileId: string): Promise<string | null> {
    const creds = profileCredentials[profileId];
    if (!creds) return null;
    try {
      const body = new URLSearchParams({
        client_id: 'coop-frontend',
        grant_type: 'password',
        username: creds.user,
        password: creds.pass,
      });
      const res = await fetch('http://localhost:8180/realms/coop-onboarding/protocol/openid-connect/token', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: body.toString(),
      });
      if (res.ok) {
        const data = await res.json();
        setToken(data.access_token);
        if (data.refresh_token) {
          setRefreshToken(data.refresh_token);
        }
        return data.access_token;
      }
    } catch (err) {
      console.warn('Keycloak offline ou erro ao obter token:', err);
    }
    return null;
  }

  async function refreshKeycloakToken(): Promise<string | null> {
    if (refreshToken.value) {
      try {
        const body = new URLSearchParams({
          client_id: 'coop-frontend',
          grant_type: 'refresh_token',
          refresh_token: refreshToken.value,
        });
        const res = await fetch('http://localhost:8180/realms/coop-onboarding/protocol/openid-connect/token', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: body.toString(),
        });
        if (res.ok) {
          const data = await res.json();
          setToken(data.access_token);
          if (data.refresh_token) {
            setRefreshToken(data.refresh_token);
          }
          console.info('Token Keycloak renovado com sucesso via Refresh Token Flow.');
          return data.access_token;
        }
      } catch (err) {
        console.warn('Erro ao renovar token via refresh_token:', err);
      }
    }

    // Fallback: re-autentica via credenciais do perfil simulado ativo
    return syncKeycloakToken(currentUser.value.id);
  }

  async function switchProfile(profileId: string) {
    const found = mockProfiles.find((p) => p.id === profileId);
    if (found) {
      currentUser.value = { ...found };
      await syncKeycloakToken(profileId);
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

  function setRefreshToken(newRefreshToken: string | null) {
    refreshToken.value = newRefreshToken;
    if (newRefreshToken) {
      localStorage.setItem('coop_refresh_token', newRefreshToken);
    } else {
      localStorage.removeItem('coop_refresh_token');
    }
  }

  return {
    currentUser,
    token,
    refreshToken,
    mockProfiles,
    isColaborador,
    isGestor,
    isAdmin,
    canGenerateQuiz,
    roleBadge,
    switchProfile,
    syncKeycloakToken,
    refreshKeycloakToken,
    setRole,
    setToken,
    setRefreshToken,
  };
});
