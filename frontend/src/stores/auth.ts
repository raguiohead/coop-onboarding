import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { UserProfile, UserRole } from '@/types';
import { api } from '@/api/client';

// Perfil de fallback para visitante não autenticado
export const guestUser: UserProfile = {
  id: '',
  name: 'Visitante Desautenticado',
  email: '',
  role: 'COLABORADOR',
  department: 'Acesso Restrito',
  avatarUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80',
  joinDate: '',
};

export const defaultMockProfiles: UserProfile[] = [
  {
    id: '6cc3d873-5688-4063-8d52-e88c8421488b',
    name: 'Lucas Almeida',
    email: 'lucas.colaborador@coop.local',
    role: 'COLABORADOR',
    department: 'Atendimento & Cooperados',
    avatarUrl: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80',
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

/**
 * Resolve dinamicamente a URL base do Keycloak conforme o ambiente de execução:
 * - Docker / Nginx Proxy: usa `/auth/` que é roteado via Nginx internamente.
 * - Vite dev server local: acessa `http://localhost:8180` diretamente.
 */
export function getKeycloakUrl(path: string): string {
  const cleanPath = path.startsWith('/') ? path : '/' + path;
  if (typeof window !== 'undefined') {
    const port = window.location.port;
    if (!port || port === '80' || port === '443' || window.location.hostname !== 'localhost') {
      return `${window.location.origin}/auth${cleanPath}`;
    }
  }
  return `http://localhost:8180${cleanPath}`;
}

export const useAuthStore = defineStore('auth', () => {
  // Sessão corporativa armazenada em sessionStorage para garantir que nova sessão inicie na tela de login
  let initialToken: string | null = null;
  let initialRefreshToken: string | null = null;
  let initialUser: UserProfile = { ...guestUser };

  if (typeof window !== 'undefined' && window.sessionStorage) {
    initialToken = sessionStorage.getItem('coop_auth_token');
    initialRefreshToken = sessionStorage.getItem('coop_refresh_token');
    const savedUserRaw = sessionStorage.getItem('coop_current_user');
    if (savedUserRaw) {
      try {
        const parsed = JSON.parse(savedUserRaw) as UserProfile;
        if (parsed && parsed.id) {
          initialUser = parsed;
        }
      } catch {
        sessionStorage.removeItem('coop_current_user');
      }
    }
  }

  const profiles = ref<UserProfile[]>([...defaultMockProfiles]);
  const currentUser = ref<UserProfile>(initialUser);
  const token = ref<string | null>(initialToken);
  const refreshToken = ref<string | null>(initialRefreshToken);
  const activeUsername = ref<string>('');

  function setCurrentUser(user: UserProfile) {
    currentUser.value = { ...user };
    if (typeof window !== 'undefined' && window.sessionStorage) {
      sessionStorage.setItem('coop_current_user', JSON.stringify(currentUser.value));
    }
  }

  function setToken(newToken: string | null) {
    token.value = newToken;
    if (typeof window !== 'undefined' && window.sessionStorage) {
      if (newToken) {
        sessionStorage.setItem('coop_auth_token', newToken);
        api.setToken(newToken);
      } else {
        sessionStorage.removeItem('coop_auth_token');
        api.setToken(null);
      }
    }
  }

  function setRefreshToken(newRefreshToken: string | null) {
    refreshToken.value = newRefreshToken;
    if (typeof window !== 'undefined' && window.sessionStorage) {
      if (newRefreshToken) {
        sessionStorage.setItem('coop_refresh_token', newRefreshToken);
      } else {
        sessionStorage.removeItem('coop_refresh_token');
      }
    }
  }

  // Inicializa o ApiClient com o token da sessão se existir
  if (token.value) {
    api.setToken(token.value);
  }

  // Interceptor para renovação automática de token expirado
  api.setRefreshTokenHandler(refreshKeycloakToken);

  const isAuthenticated = computed(() => !!token.value && !!currentUser.value.id);
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

  /**
   * Valida ativamente a autenticidade e validade do token JWT de acordo com o Keycloak.
   * Realiza verificação de expiração local e consulta o endpoint UserInfo oficial.
   */
  async function validateTokenWithKeycloak(): Promise<boolean> {
    const currentToken = token.value;
    if (!currentToken) {
      return false;
    }

    // 1. Verificação prévia de expiração local no timestamp exp do payload JWT
    try {
      const parts = currentToken.split('.');
      if (parts.length === 3) {
        const payloadJson = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
        if (payloadJson.exp && Date.now() >= payloadJson.exp * 1000) {
          console.warn('[Keycloak Auth] Token JWT expirado localmente. Disparando renovação...');
          const renewedToken = await refreshKeycloakToken();
          if (!renewedToken) {
            logout();
            return false;
          }
        }
      }
    } catch (e) {
      console.warn('[Keycloak Auth] Falha ao inspecionar exp do token:', e);
    }

    // 2. Validação ativa e autoritativa no Keycloak IAM (/protocol/openid-connect/userinfo)
    try {
      const userinfoUrl = getKeycloakUrl('/realms/coop-onboarding/protocol/openid-connect/userinfo');
      const res = await fetch(userinfoUrl, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token.value}`,
        },
      });

      if (res.ok) {
        const userInfo = await res.json();
        if (userInfo && userInfo.preferred_username) {
          activeUsername.value = userInfo.preferred_username;
        }
        return true;
      } else if (res.status === 401) {
        console.warn('[Keycloak Auth] UserInfo retornou 401 Unauthorized. Tentando refresh...');
        const renewedToken = await refreshKeycloakToken();
        if (renewedToken) {
          return true;
        }
        logout();
        return false;
      }
    } catch (err) {
      console.warn('[Keycloak Auth] Não foi possível contatar o Keycloak para validar o token:', err);
      // Em caso de falha de rede transitória, confere o timestamp local
      try {
        const parts = token.value?.split('.') || [];
        if (parts.length === 3) {
          const payloadJson = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
          if (payloadJson.exp && Date.now() < payloadJson.exp * 1000) {
            return true;
          }
        }
      } catch {}
      logout();
      return false;
    }

    return false;
  }

  /**
   * Autenticação OAuth2 Resource Owner Password Credentials no Keycloak IAM.
   */
  async function login(userParam: string, pass: string): Promise<boolean> {
    const cleanUser = userParam.trim();
    try {
      const tokenUrl = getKeycloakUrl('/realms/coop-onboarding/protocol/openid-connect/token');
      const body = new URLSearchParams({
        client_id: 'coop-frontend',
        grant_type: 'password',
        username: cleanUser,
        password: pass,
      });

      const res = await fetch(tokenUrl, {
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
        activeUsername.value = cleanUser;

        // 1. Sincroniza com o backend /api/v1/users/me (PostgreSQL)
        try {
          const userMe = await api.getCurrentUser();
          if (userMe) {
            const matched = profiles.value.find((p) => p.email.toLowerCase() === userMe.email.toLowerCase());
            setCurrentUser({
              id: userMe.id,
              name: userMe.name,
              email: userMe.email,
              role: userMe.role as UserRole,
              department: userMe.department || matched?.department || 'Geral',
              avatarUrl: matched?.avatarUrl || (userMe.role === 'GESTOR'
                ? 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150&auto=format&fit=crop&q=80'
                : userMe.role === 'ADMIN'
                ? 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80'
                : 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80'),
              joinDate: matched?.joinDate || '30/09/2026',
            });
            return true;
          }
        } catch (err) {
          console.warn('Fallback perfil local após login:', err);
        }

        // 2. Mapeamento de fallback
        const credKey = Object.keys(profileCredentials).find(k => k.toLowerCase() === cleanUser.toLowerCase());
        if (credKey) {
          const profileId = profileCredentials[credKey].profileId;
          const found = profiles.value.find(p => p.id === profileId);
          if (found) {
            setCurrentUser(found);
            return true;
          }
        }

        // 3. Fallback genérico para novo usuário
        setCurrentUser({
          id: `usr-${Date.now()}`,
          name: cleanUser,
          email: `${cleanUser}@coop.local`,
          role: cleanUser.includes('admin') ? 'ADMIN' : cleanUser.includes('gestor') ? 'GESTOR' : 'COLABORADOR',
          department: 'Operações & Atendimento',
          avatarUrl: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80',
          joinDate: '30/09/2026',
        });
        return true;
      }
    } catch (err) {
      console.error('Falha ao autenticar no Keycloak:', err);
    }
    return false;
  }

  async function refreshKeycloakToken(): Promise<string | null> {
    if (refreshToken.value) {
      try {
        const tokenUrl = getKeycloakUrl('/realms/coop-onboarding/protocol/openid-connect/token');
        const body = new URLSearchParams({
          client_id: 'coop-frontend',
          grant_type: 'refresh_token',
          refresh_token: refreshToken.value,
        });
        const res = await fetch(tokenUrl, {
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
          console.info('[Keycloak Auth] Token renovado com sucesso via refresh_token.');
          return data.access_token;
        }
      } catch (err) {
        console.warn('[Keycloak Auth] Erro ao renovar token via refresh_token:', err);
      }
    }
    return null;
  }

  function logout() {
    setToken(null);
    setRefreshToken(null);
    currentUser.value = { ...guestUser };
    activeUsername.value = '';
    if (typeof window !== 'undefined') {
      sessionStorage.removeItem('coop_auth_token');
      sessionStorage.removeItem('coop_refresh_token');
      sessionStorage.removeItem('coop_current_user');
      localStorage.removeItem('coop_auth_token');
      localStorage.removeItem('coop_refresh_token');
      localStorage.removeItem('coop_current_user');
    }
    api.setToken(null);
  }

  async function fetchCurrentUser(): Promise<UserProfile | null> {
    if (!token.value) return null;
    try {
      const userMe = await api.getCurrentUser();
      if (userMe) {
        const matched = profiles.value.find((p) => p.email.toLowerCase() === userMe.email.toLowerCase());
        const updated: UserProfile = {
          id: userMe.id,
          name: userMe.name,
          email: userMe.email,
          role: userMe.role as UserRole,
          department: userMe.department || matched?.department || 'Geral',
          avatarUrl: matched?.avatarUrl || (userMe.role === 'GESTOR'
            ? 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150&auto=format&fit=crop&q=80'
            : userMe.role === 'ADMIN'
            ? 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80'
            : 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80'),
          joinDate: matched?.joinDate || '30/09/2026',
        };
        setCurrentUser(updated);
        return updated;
      }
    } catch (err) {
      console.warn('Erro ao sincronizar perfil do backend via /users/me:', err);
    }
    return currentUser.value;
  }

  async function switchProfile(profileId: string) {
    const found = profiles.value.find((p) => p.id === profileId);
    if (found) {
      const username = profileIdToUsername[profileId];
      if (username && profileCredentials[username]) {
        await login(profileCredentials[username].user, profileCredentials[username].pass);
      } else {
        setCurrentUser(found);
      }
    }
  }

  function setRole(role: UserRole) {
    currentUser.value.role = role;
    if (typeof window !== 'undefined' && window.sessionStorage) {
      sessionStorage.setItem('coop_current_user', JSON.stringify(currentUser.value));
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
    validateTokenWithKeycloak,
    fetchCurrentUser,
    switchProfile,
    refreshKeycloakToken,
    setRole,
    setToken,
    setRefreshToken,
  };
});
