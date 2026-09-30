import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { UserProfile, UserRole } from '@/types';
import { api } from '@/api/client';

export const defaultMockProfiles: UserProfile[] = [
  {
    id: '6cc3d873-5688-4063-8d52-e88c8421488b',
    name: 'Lucas Almeida',
    email: 'lucas.colaborador@coop.local',
    role: 'COLABORADOR',
    department: 'Atendimento & Cooperados',
    avatarUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80',
    joinDate: '30/09/2026',
  },
  {
    id: '670a8cad-248b-41bb-86ff-adb72cab13cb',
    name: 'Mariana Ribeiro',
    email: 'mariana.gestora@coop.local',
    role: 'GESTOR',
    department: 'Desenvolvimento Humano e Organizacional (DHO)',
    avatarUrl: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150&auto=format&fit=crop&q=80',
    joinDate: '30/09/2026',
  },
  {
    id: '07c89e60-4ea6-4ab0-ac0f-96c0fef51e88',
    name: 'Rodrigo Martins',
    email: 'rodrigo.admin@coop.local',
    role: 'ADMIN',
    department: 'Governança & TI',
    avatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80',
    joinDate: '30/09/2026',
  },
];

export const mockProfiles = defaultMockProfiles;

export const profileCredentials: Record<string, { user: string; pass: string; profileId: string }> = {
  'lucas.colaborador': { user: 'lucas.colaborador', pass: 'Colab@123', profileId: '6cc3d873-5688-4063-8d52-e88c8421488b' },
  'mariana.gestora': { user: 'mariana.gestora', pass: 'Gestor@123', profileId: '670a8cad-248b-41bb-86ff-adb72cab13cb' },
  'rodrigo.admin': { user: 'rodrigo.admin', pass: 'Admin@123', profileId: '07c89e60-4ea6-4ab0-ac0f-96c0fef51e88' },
};

const profileIdToUsername: Record<string, string> = {
  '6cc3d873-5688-4063-8d52-e88c8421488b': 'lucas.colaborador',
  '670a8cad-248b-41bb-86ff-adb72cab13cb': 'mariana.gestora',
  '07c89e60-4ea6-4ab0-ac0f-96c0fef51e88': 'rodrigo.admin',
};

export const useAuthStore = defineStore('auth', () => {
  // Inicialização segura: se houver perfis legados em cache que não contêm os novos IDs, reseta
  const savedProfilesRaw = localStorage.getItem('coop_user_profiles');
  let initialProfiles = [...defaultMockProfiles];
  if (savedProfilesRaw) {
    try {
      const parsed = JSON.parse(savedProfilesRaw) as UserProfile[];
      const hasValidUsers = parsed.some(p => p.email.endsWith('@coop.local') && ['lucas.colaborador@coop.local', 'mariana.gestora@coop.local', 'rodrigo.admin@coop.local'].includes(p.email));
      if (hasValidUsers && parsed.length === 3) {
        initialProfiles = parsed;
      } else {
        localStorage.removeItem('coop_user_profiles');
        localStorage.removeItem('coop_current_user');
      }
    } catch {
      localStorage.removeItem('coop_user_profiles');
    }
  }
  const profiles = ref<UserProfile[]>(initialProfiles);

  const savedCurrentUser = localStorage.getItem('coop_current_user');
  const currentUser = ref<UserProfile>(
    savedCurrentUser ? JSON.parse(savedCurrentUser) : profiles.value[0]
  );
  const token = ref<string | null>(localStorage.getItem('coop_auth_token'));
  const refreshToken = ref<string | null>(localStorage.getItem('coop_refresh_token'));
  const activeUsername = ref<string>('lucas.colaborador');

  function setCurrentUser(user: UserProfile) {
    currentUser.value = { ...user };
    localStorage.setItem('coop_current_user', JSON.stringify(currentUser.value));
  }

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
        setCurrentUser(profiles.value[idx]);
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

  function getKeycloakTokenUrl(): string {
    if (typeof window !== 'undefined' && window.location.hostname !== 'localhost') {
      return `${window.location.origin}/auth/realms/coop-onboarding/protocol/openid-connect/token`;
    }
    return 'http://localhost:8180/realms/coop-onboarding/protocol/openid-connect/token';
  }

  async function login(username: string, pass: string): Promise<boolean> {
    try {
      const body = new URLSearchParams({
        client_id: 'coop-frontend',
        grant_type: 'password',
        username,
        password: pass,
      });

      const res = await fetch(getKeycloakTokenUrl(), {
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

        // Mapeia usuário do Keycloak para o perfil da aplicação
        const credKey = Object.keys(profileCredentials).find(k => k.toLowerCase() === username.toLowerCase());
        if (credKey) {
          const profileId = profileCredentials[credKey].profileId;
          const found = mockProfiles.find(p => p.id === profileId);
          if (found) {
            setCurrentUser(found);
          }
        }

        // Sincroniza com os dados reais do banco PostgreSQL via /api/v1/users/me
        try {
          const userMe = await api.getCurrentUser();
          if (userMe) {
            const matched = mockProfiles.find(p => p.email === userMe.email);
            setCurrentUser({
              id: userMe.id,
              name: userMe.name,
              email: userMe.email,
              role: userMe.role as UserRole,
              department: userMe.department || matched?.department || 'Geral',
              avatarUrl: matched?.avatarUrl || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80',
              joinDate: '30/09/2026',
            });
          }
        } catch (err) {
          console.warn('Fallback para perfil local (falha ao chamar /users/me):', err);
        }

        return true;
      }
    } catch (err) {
      console.error('Falha ao autenticar no Keycloak:', err);
    }
    return false;
  }

  async function syncKeycloakToken(profileId: string): Promise<string | null> {
    const username = profileIdToUsername[profileId] || 'lucas.colaborador';
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
        const res = await fetch(getKeycloakTokenUrl(), {
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
      setCurrentUser(found);
      await syncKeycloakToken(profileId);
    }
  }

  function logout() {
    setToken(null);
    setRefreshToken(null);
    localStorage.removeItem('coop_auth_token');
    localStorage.removeItem('coop_refresh_token');
    localStorage.removeItem('coop_current_user');
    api.setToken(null);
  }

  function setRole(role: UserRole) {
    currentUser.value.role = role;
    localStorage.setItem('coop_current_user', JSON.stringify(currentUser.value));
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
