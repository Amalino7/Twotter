import { defineStore } from 'pinia';

interface UIState {
  showModal: boolean;
  modalProps: { url?: string };
  toastType: 'success' | 'info' | 'warning' | 'error';
  toastMessage: string;
  showToast: boolean;
}

export const useUIStore = defineStore('ui', {
  state: (): UIState => ({
    showModal: false,
    modalProps: { url: 'https://localhost' },
    showToast: false,
    toastMessage: '',
    toastType: 'info', // 'success', 'error', etc.
  }),
  actions: {
    openModal(props: {}) {
      this.showModal = true;
      this.modalProps = props;
    },
    closeModal() {
      this.modalProps = {};
    },
    triggerToast(message: string, type: 'success' | 'info' | 'warning' | 'error' = 'info') {
      this.toastMessage = message;
      this.toastType = type;
      this.showToast = true;
    },
  },
});
