import { createRouter, createWebHistory } from 'vue-router';
import DashboardView from '@/views/DashboardView.vue';
import LessonView from '@/views/LessonView.vue';
import QuizzesView from '@/views/QuizzesView.vue';
import ManagementView from '@/views/ManagementView.vue';
import ProfileView from '@/views/ProfileView.vue';
import LoginView from '@/views/LoginView.vue';
import CodeOfConductView from '@/views/CodeOfConductView.vue';
import SecurityPrivacyView from '@/views/SecurityPrivacyView.vue';
import SupportView from '@/views/SupportView.vue';
import { useAuthStore } from '@/stores/auth';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { public: true },
    },
    {
      path: '/',
      name: 'dashboard',
      component: DashboardView,
    },
    {
      path: '/tracks/:trackId/lessons/:lessonId',
      name: 'lesson',
      component: LessonView,
    },
    {
      path: '/quizzes',
      name: 'quizzes',
      component: QuizzesView,
    },
    {
      path: '/gestao',
      name: 'gestao',
      component: ManagementView,
    },
    {
      path: '/perfil',
      name: 'perfil',
      component: ProfileView,
    },
    {
      path: '/codigo-conduta',
      name: 'codigo-conduta',
      component: CodeOfConductView,
    },
    {
      path: '/seguranca-privacidade',
      name: 'seguranca-privacidade',
      component: SecurityPrivacyView,
    },
    {
      path: '/suporte',
      name: 'suporte',
      component: SupportView,
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/',
    },
  ],
  scrollBehavior() {
    return { top: 0 };
  },
});

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  if (to.meta.public) {
    next();
    return;
  }
  // Se não possuir token ativo, sincroniza silenciosamente com o perfil atual
  if (!authStore.token) {
    await authStore.syncKeycloakToken(authStore.currentUser.id);
  }
  next();
});

export default router;
