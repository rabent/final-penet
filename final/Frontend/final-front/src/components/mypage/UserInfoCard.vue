<template>
  <div class="user-info-card">
    <div class="card-header">
      <h2>회원 정보</h2>
      <div class="action-buttons" v-if="!isEditing">
        <button @click="$emit('edit-start')" class="edit-btn">정보 수정</button>
        <button @click="$emit('delete-account')" class="delete-btn">회원 탈퇴</button>
      </div>
      <div class="action-buttons" v-else>
        <button @click="handleSave" class="save-btn" :disabled="!isFormValid">저장</button>
        <button @click="$emit('edit-cancel')" class="cancel-btn">취소</button>
      </div>
    </div>

    <div class="card-content">
      <form @submit.prevent="handleSave">
        <!-- 이메일 필드 (수정 불가) -->
        <div class="form-group">
          <label for="email">이메일 (아이디)</label>
          <input
            type="email"
            id="email"
            v-model="editedUser.email"
            disabled
            class="disabled"
          />
        </div>

        <!-- 이름 필드 -->
        <div class="form-group">
          <label for="name">이름</label>
          <input
            type="text"
            id="name"
            v-model="editedUser.name"
            :disabled="!isEditing"
            :class="{ disabled: !isEditing }"
            required
            maxlength="15"
          />
          <span class="input-guide" v-if="isEditing && !isNameValid">
            이름은 15자 이하로 입력해주세요.
          </span>
        </div>

        <!-- 비밀번호 필드 (수정 모드에서만 표시) -->
        <div class="form-group" v-if="isEditing">
          <label for="password">새 비밀번호 (변경 시에만 입력)</label>
          <input
            type="password"
            id="password"
            v-model="editedUser.password"
            placeholder="8-15자 사이로 입력하세요"
            minlength="8"
            maxlength="15"
          />
          <span class="input-guide" v-if="editedUser.password && !isPasswordValid">
            비밀번호는 8자 이상 15자 이하로 입력해주세요.
          </span>
        </div>

        <!-- 비밀번호 확인 필드 (비밀번호 입력시에만 표시) -->
        <div class="form-group" v-if="isEditing && editedUser.password">
          <label for="passwordConfirm">비밀번호 확인</label>
          <input
            type="password"
            id="passwordConfirm"
            v-model="passwordConfirm"
            placeholder="비밀번호를 다시 입력하세요"
          />
          <span class="password-match" :class="{ 'not-match': !isPasswordMatch && passwordConfirm }">
            {{ passwordMatchMessage }}
          </span>
        </div>

        <!-- 주소 필드 -->
        <div class="form-group">
          <label for="address">주소</label>
          <input
            type="text"
            id="address"
            v-model="editedUser.address"
            :disabled="!isEditing"
            :class="{ disabled: !isEditing }"
            required
            maxlength="50"
          />
          <span class="input-guide" v-if="isEditing && !isAddressValid">
            주소는 50자 이하로 입력해주세요.
          </span>
        </div>

        <!-- 전화번호 필드 -->
        <div class="form-group">
          <label for="number">전화번호</label>
          <input
            type="tel"
            id="number"
            v-model="editedUser.number"
            :disabled="!isEditing"
            :class="{ disabled: !isEditing }"
            required
            maxlength="11"
            @input="validatePhoneNumber"
          />
          <span class="input-guide" v-if="isEditing && !isPhoneNumberValid">
            올바른 전화번호 형식이 아닙니다. (예: 01012345678)
          </span>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import api from '@/utils/axios';

const props = defineProps({
  user: {
    type: Object,
    required: true
  },
  isEditing: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['edit-start', 'edit-cancel', 'edit-save', 'delete-account'])

// 편집 중인 사용자 정보를 저장할 반응형 객체
const editedUser = reactive({
  name: '',
  email: '',
  address: '',
  number: '',
  password: ''
})

// 비밀번호 확인
const passwordConfirm = ref('')

// props.user가 변경될 때 editedUser 업데이트
watch(() => props.user, (newUser) => {
  Object.assign(editedUser, {
    name: newUser.name || '',
    email: newUser.email || '',
    address: newUser.address || '',
    number: newUser.number || '',
    password: ''  // 비밀번호는 항상 빈 값으로 초기화
  })
}, { immediate: true, deep: true })

// 전화번호 유효성 검사
const validatePhoneNumber = (event) => {
  // 숫자만 입력 가능하도록
  editedUser.number = event.target.value.replace(/[^0-9]/g, '').slice(0, 11)
}

const isPhoneNumberValid = computed(() => {
  const phoneRegex = /^010\d{8}$/
  return !editedUser.number || phoneRegex.test(editedUser.number)
})

// 이름 유효성 검사
const isNameValid = computed(() => {
  return !editedUser.name || editedUser.name.length <= 15
})

// 주소 유효성 검사
const isAddressValid = computed(() => {
  return !editedUser.address || editedUser.address.length <= 50
})

// 비밀번호 유효성 검사
const isPasswordValid = computed(() => {
  return !editedUser.password ||
    (editedUser.password.length >= 8 && editedUser.password.length <= 15)
})

// 비밀번호 일치 확인
const isPasswordMatch = computed(() => {
  return !editedUser.password || !passwordConfirm.value ||
    editedUser.password === passwordConfirm.value
})

const passwordMatchMessage = computed(() => {
  if (!passwordConfirm.value) return ''
  return isPasswordMatch.value ? '비밀번호가 일치합니다.' : '비밀번호가 일치하지 않습니다.'
})

// 폼 유효성 검사
const isFormValid = computed(() => {
  // 기본 필드 검사
  const baseValidation = isNameValid.value &&
                         isAddressValid.value &&
                         isPhoneNumberValid.value

  // 비밀번호가 입력된 경우 추가 검사
  if (editedUser.password) {
    return baseValidation && isPasswordValid.value &&
           passwordConfirm.value && editedUser.password === passwordConfirm.value
  }

  return baseValidation
})

// 저장 버튼 핸들러
const handleSave = () => {
  if (!isFormValid.value) {
    return
  }

  // 비밀번호가 입력되지 않았다면 객체에서 제거 (서버에 불필요한 데이터 전송 방지)
  const userToSave = { ...editedUser }
  if (!userToSave.password) {
    delete userToSave.password
  }

  emit('edit-save', userToSave)
}
</script>

<style scoped>
.user-info-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.card-header {
  padding: 20px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.card-content {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.form-group input:focus {
  border-color: #3498db;
  outline: none;
}

.form-group input.disabled {
  background-color: #f9f9f9;
  color: #666;
  cursor: not-allowed;
}

.input-guide {
  display: block;
  margin-top: 5px;
  font-size: 14px;
  color: #e74c3c;
}

.password-match {
  display: block;
  margin-top: 5px;
  font-size: 14px;
  color: #27ae60;
}

.password-match.not-match {
  color: #e74c3c;
}

.edit-btn {
  padding: 8px 16px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.edit-btn:hover {
  background-color: #2980b9;
}

.delete-btn {
  padding: 8px 16px;
  background-color: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.delete-btn:hover {
  background-color: #c0392b;
}

.save-btn {
  padding: 8px 16px;
  background-color: #2ecc71;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.save-btn:hover {
  background-color: #27ae60;
}

.save-btn:disabled {
  background-color: #95a5a6;
  cursor: not-allowed;
}

.cancel-btn {
  padding: 8px 16px;
  background-color: #95a5a6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.cancel-btn:hover {
  background-color: #7f8c8d;
}
</style>
