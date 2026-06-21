export interface User {
  id: number;          // correspond à userId
  email: string;
  role: 'ADMIN' | 'LEARNER' | 'INSTRUCTOR'; // roles possibles
  nom: string;
  prenom: string;
  photoUrl?: string;
  bio?: string;
  createdAt?: string;
  enabled: boolean;
  
}
export interface UpdateUserRequest {
  nom?: string;
  prenom?: string;
  bio?: string;
  photoUrl?: string;
}

export interface PageUser {
  content: User[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}