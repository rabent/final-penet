<template>
  <div class="mypage-container">
    <div class="mypage-header">
      <h1>마이페이지</h1>
      <p>{{ user.name }}님의 회원 정보</p>
    </div>

    <div class="mypage-content">
      <!-- 왼쪽 섹션: 사용자 정보 -->
      <div class="user-info-section">
        <UserInfoCard
          :user="user"
          :isEditing="isEditing"
          @edit-start="startEditing"
          @edit-cancel="cancelEditing"
          @edit-save="saveUserInfo"
          @delete-account="showDeleteConfirm = true"
        />
      </div>

      <!-- 오른쪽 섹션: 내가 쓴 글 목록 -->
      <div class="user-posts-section" v-if="showUserPosts">
        <div class="posts-header">
          <h2>내가 쓴 글</h2>
          <button @click="toggleUserPosts" class="toggle-posts-btn">
            숨기기
          </button>
        </div>
        <UserPostsList :userId="userId" />
      </div>
      <div v-else class="show-posts-button-container">
        <button @click="toggleUserPosts" class="toggle-posts-btn show">
          내가 쓴 글 보기
        </button>
      </div>
    </div>

    <!-- 회원 탈퇴 확인 모달 -->
    <div class="modal-backdrop" v-if="showDeleteConfirm" @click="showDeleteConfirm = false">
      <div class="modal-content" @click.stop>
        <h3>회원 탈퇴</h3>
        <p>정말로 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.</p>
        <div class="modal-buttons">
          <button @click="showDeleteConfirm = false" class="cancel-btn">취소</button>
          <button @click="deleteAccount" class="delete-btn">탈퇴하기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/axios';
import UserInfoCard from '@/components/mypage/UserInfoCard.vue'
import UserPostsList from '@/components/mypage/UserPostsList.vue'

const router = useRouter()
const isEditing = ref(false)
const showDeleteConfirm = ref(false)
const showUserPosts = ref(true)
const userId = ref('')

// 사용자 정보를 저장할 반응형 객체
const user = reactive({
  name: '',
  email: '',
  address: '',
  number: ''
})

// 페이지 로드 시 사용자 정보 가져오기
onMounted(async () => {
  // 세션 스토리지에서 userId 가져오기
  userId.value = sessionStorage.getItem('userId')

  if (!userId.value) {
    alert('로그인이 필요한 서비스입니다.')
    router.push('/login')
    return
  }

  try {
    const response = await api.get(`http://localhost:8080/api/users/${userId.value}`, {
      headers: {
        Authorization: sessionStorage.getItem('token')
      }
    })

    // 응답에서 사용자 정보 설정
    const userData = response.data
    user.name = userData.name
    user.email = userData.email
    user.address = userData.address
    user.number = userData.number ? userData.number.replace(/-/g, '') : ''

  } catch (error) {
    console.error('사용자 정보 조회 실패:', error)
    if (error.response && error.response.status === 401) {
      // 인증 오류 처리는 axios 인터셉터에서 처리
    } else {
      alert('사용자 정보를 불러오는데 실패했습니다.')
    }
  }
})

// 편집 모드 시작
const startEditing = () => {
  isEditing.value = true
}

// 편집 취소
const cancelEditing = () => {
  isEditing.value = false
}

// 사용자 정보 업데이트
const saveUserInfo = async (updatedUser) => {
  try {
    const formattedUser = {
      ...updatedUser,
      number: formatPhoneNumber(updatedUser.number)
    }

    await api.put(`http://localhost:8080/api/users/${userId.value}`, formattedUser, {
      headers: {
        Authorization: sessionStorage.getItem('token')
      }
    })

    // 업데이트된 정보로 user 객체 갱신
    Object.assign(user, updatedUser)
    isEditing.value = false
    alert('회원 정보가 성공적으로 수정되었습니다.')

  } catch (error) {
    console.error('회원 정보 수정 실패:', error)
    alert('회원 정보 수정에 실패했습니다.')
  }
}

// 회원 탈퇴
const deleteAccount = async () => {
  try {
    await api.delete(`http://localhost:8080/api/users/${userId.value}`, {
      headers: {
        Authorization: sessionStorage.getItem('token')
      }
    })

    // 세션 스토리지 비우기
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('userId')

    alert('회원 탈퇴가 완료되었습니다.')
    router.push('/')

  } catch (error) {
    console.error('회원 탈퇴 실패:', error)
    alert('회원 탈퇴에 실패했습니다.')
  }
}

// 게시글 목록 토글
const toggleUserPosts = () => {
  showUserPosts.value = !showUserPosts.value
}

// 전화번호 포맷팅 (서버 전송용)
const formatPhoneNumber = (number) => {
  if (number && number.length === 11) {
    return `${number.slice(0, 3)}-${number.slice(3, 7)}-${number.slice(7)}`
  }
  return number
}
</script>

<style scoped>
.mypage-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.mypage-header {
  margin-bottom: 30px;
  text-align: center;
}

.mypage-header h1 {
  font-size: 32px;
  color: #333;
  margin-bottom: 10px;
}

.mypage-header p {
  font-size: 18px;
  color: #666;
}

.mypage-content {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
}

.user-info-section {
  flex: 1;
  min-width: 300px;
}

.user-posts-section {
  flex: 1;
  min-width: 300px;
}

.posts-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.posts-header h2 {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.toggle-posts-btn {
  padding: 8px 16px;
  background-color: #f0f0f0;
  border: none;
  border-radius: 4px;
  color: #333;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.toggle-posts-btn:hover {
  background-color: #e0e0e0;
}

.toggle-posts-btn.show {
  background-color: #3498db;
  color: white;
}

.toggle-posts-btn.show:hover {
  background-color: #2980b9;
}

.show-posts-button-container {
  display: flex;
  justify-content: center;
  width: 100%;
  margin-top: 20px;
}

/* 모달 스타일 */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  max-width: 400px;
  width: 100%;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.modal-content h3 {
  margin-top: 0;
  color: #333;
  font-size: 24px;
  margin-bottom: 15px;
}

.modal-content p {
  color: #666;
  margin-bottom: 25px;
}

.modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}

.cancel-btn {
  padding: 10px 20px;
  background-color: #f0f0f0;
  color: #333;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.delete-btn {
  padding: 10px 20px;
  background-color: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.delete-btn:hover {
  background-color: #c0392b;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .mypage-content {
    flex-direction: column;
  }

  .user-info-section,
  .user-posts-section {
    min-width: 100%;
  }
}
</style>
