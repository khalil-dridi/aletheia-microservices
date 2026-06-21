export interface InstructorRequest {
  id: number;
  userId: number;
  userName: string;
  cvUrl: string;
  score?: number | null;
  aiFeedback?: string | null;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  createdAt: string;
  updatedAt?: string | null;
}