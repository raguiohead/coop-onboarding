import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { UserProfile, UserRole } from '@/types';
import { api } from '@/api/client';

export const defaultMockProfiles: UserProfile[] = [
  {
    id: '11111111-1111-1111-1111-111111111111',
    name: 'Ana Carolina Silva',
    email: 'ana.silva@coop.local',
    role: 'COLABORADOR',
    department: 'Atendimento & Cooperados',
    avatarUrl: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80',
    joinDate: '15/09/2026',
  },
  {
    id: '44444444-4444-4444-4444-444444444444',
    name: 'Carlos Souza',
    email: 'carlos.souza@coop.local',
    role: 'COLABORADOR',
    department: 'Engenharia de TI & Inovação',
    avatarUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80',
    joinDate: '20/09/2026',
  },
  {
    id: '22222222-2222-2222-2222-222222222222',
    name: 'Roberto Mendes',
    email: 'roberto.mendes@coop.local',
    role: 'GESTOR',
    department: 'Gente & Gestão (RH)',
    avatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80',
    joinDate: '10/01/2024',
  },
  {
    id: '55555555-5555-5555-5555-555555555555',
    name: 'Fernanda Lima',
    email: 'fernanda.lima@coop.local',
    role: 'GESTOR',
    department: 'Gerência de Crédito & Riscos',
    avatarUrl: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=150&auto=format&fit=crop&q=80',
    joinDate: '15/05/2023',
  },
  {
    id: '33333333-3333-3333-3333-333333333333',
    name: 'Mariana Duarte',
    email: 'mariana.duarte@coop.local',
    role: 'ADMIN',
    department: 'Tecnologia & Governança',
    avatarUrl: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150&auto=format&fit=crop&q=80',
    joinDate: '01/03/2023',
  },
];

export const mockProfiles = defaultMockProfiles;

export const profileCredentials: Record<string, { user: string; pass: string; profileId: string }> = {
  'colaborador': { user: 'colaborador', pass: 'colab123', profileId: '11111111-1111-1111-1111-111111111111' },
  'carlos': { user: 'carlos', pass: 'carlos123', profileId: '44444444-4444-4444-4444-444444444444' },
  'gestor': { user: 'gestor', pass: 'gestor123', profileId: '22222222-2222-2222-2222-222222222222' },
  'fernanda': { user: 'fernanda', pass: 'fernanda123', profileId: '55555555-5555-5555-5555-555555555555' },
  'admin': { user: 'admin', pass: 'admin123', profileId: '33333333-3333-3333-3333-333333333333' },
};

const profileIdToUsername: Record<string, string> = {
  '11111111-1111-1111-1111-111111111111': 'colaborador',
  '44444444-4444-4444-4444-444444444444': 'carlos',
  '22222222-2222-2222-2222-222222222222': 'gestor',
  '55555555-5555-5555-5555-555555555555': 'fernanda',
  '33333333-3333-3333-3333-333333333333': 'admin',
};

export const useAuthStore = defineStore('auth', () => {
  const savedProfiles = localStorage.getItem('coop_user_profiles');
  const profiles = ref<UserProfile[]>(savedProfiles ? JSON.parse(savedProfiles) : [...defaultMockProfiles]);

  const currentUser = ref<UserProfile>(profiles.value[0]);
  const token = ref<string | null>(localStorage.getItem('coop_auth_token'));
  const refreshToken = ref<string | null>(localStorage.getItem('coop_refresh_token'));
  const activeUsername = ref<string>('colaborador');

  function saveProfiles() {
    localStorage.setItem('coop_user_profiles', JSON.stringify(profiles.value));
  }

  function createUser(userData: Omit<UserProfile, 'id'>): UserProfile {
    const newId = `user-${Date.now()}`;
    const newUser: UserProfile = {
      id: newId,
      ...userData,
    };
    profiles.value.push(newUser);
    saveProfiles();
    return newUser;
  }

  function updateUser(id: string, updates: Partial<UserProfile>) {
    const idx = profiles.value.findIndex((p) => p.id === id);
    if (idx !== -1) {
      profiles.value[idx] = { ...profiles.value[idx], ...updates };
      saveProfiles();
      if (currentUser.value.id === id) {
        currentUser.value = { ...profiles.value[idx] };
      }
    }
  }

  function deleteUser(id: string) {
    profiles.value = profiles.value.filter((p) => p.id !== id);
    saveProfiles();
    if (currentUser.value.id === id && profiles.value.length > 0) {
      currentUser.value = { ...profiles.value[0] };
    }
  }

  // Inicializa o ApiClient com o token salvo se existir
  if (token.value) {
    api.setToken(token.value);
  }

  // Registra o interceptor de renovação automática no cliente HTTP
  api.setRefreshTokenHandler(refreshKeycloakToken);

  const isAuthenticated = computed(() => !!token.value);
  const isColaborador = computed(() => currentUser.value.role === 'COLABORADOR');
  const isGestor = computed(() => currentUser.value.role === 'GESTOR');
  const isAdmin = computed(() => currentUser.value.role === 'ADMIN');
  const canGenerateQuiz = computed(() => currentUser.value.role === 'GESTOR' || currentUser.value.role === 'ADMIN');

  const roleBadge = computed(() => {
    switch (currentUser.value.role) {
      case 'ADMIN':
        return { label: 'Administrador TI & Governança', bg: 'bg-rose-100 text-rose-800 border-rose-200' };
      case 'GESTOR':
        return { label: 'Gestor de Aprendizagem & Equipe', bg: 'bg-indigo-100 text-indigo-800 border-indigo-200' };
      default:
        return { label: 'Colaborador em Formação', bg: 'bg-teal-100 text-teal-800 border-teal-200' };
    }
  });

  async function login(username: string, pass: string): Promise<boolean> {
    try {
      const body = new URLSearchParams({
        client_id: 'coop-frontend',
        grant_type: 'password',
        username,
        password: pass,
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
        activeUsername.value = username;

        // Mapeia usuário do Keycloak para o perfil mock da aplicação
        const credKey = Object.keys(profileCredentials).find(k => k === username);
        if (credKey) {
          const profileId = profileCredentials[credKey].profileId;
          const found = mockProfiles.find(p => p.id === profileId);
          if (found) {
            currentUser.value = { ...found };
          }
        }
        return true;
      }
    } catch (err) {
      console.error('Falha ao autenticar no Keycloak:', err);
    }
    return false;
  }

  async function syncKeycloakToken(profileId: string): Promise<string | null> {
    const username = profileIdToUsername[profileId] || 'colaborador';
    const cred = profileCredentials[username];
    if (!cred) return null;

    const ok = await login(cred.user, cred.pass);
    return ok ? token.value : null;
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

    return syncKeycloakToken(currentUser.value.id);
  }

  async function switchProfile(profileId: string) {
    const found = profiles.value.find((p) => p.id === profileId);
    if (found) {
      currentUser.value = { ...found };
      await syncKeycloakToken(profileId);
    }
  }

  function logout() {
    setToken(null);
    setRefreshToken(null);
    localStorage.removeItem('coop_auth_token');
    localStorage.removeItem('coop_refresh_token');
    api.setToken(null);
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
    activeUsername,
    profiles,
    mockProfiles: profiles,
    profileCredentials,
    isAuthenticated,
    isColaborador,
    isGestor,
    isAdmin,
    canGenerateQuiz,
    roleBadge,
    login,
    logout,
    switchProfile,
    syncKeycloakToken,
    refreshKeycloakToken,
    setRole,
    setToken,
    setRefreshToken,
    createUser,
    updateUser,
    deleteUser,
  };
});
