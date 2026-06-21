import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InstructorRequest } from '../models/instructor-request.model';

@Injectable({ providedIn: 'root' })
export class InstructorRequestService {

  private API = environment.apiUrl + '/instructor-request';

  constructor(private http: HttpClient) {}

  createRequest(file: File, motivation?: string): Observable<InstructorRequest> {
    const formData = new FormData();
    formData.append('file', file);

    if (motivation?.trim()) {
      formData.append('motivation', motivation.trim());
    }

    return this.http.post<InstructorRequest>(this.API, formData);
  }

  getAllRequests(): Observable<InstructorRequest[]> {
    return this.http.get<InstructorRequest[]>(`${this.API}/admin`);
  }

  approveRequest(id: number): Observable<string> {
    return this.http.put(`${this.API}/admin/${id}/approve`, {}, { responseType: 'text' });
  }

  rejectRequest(id: number): Observable<string> {
    return this.http.put(`${this.API}/admin/${id}/reject`, {}, { responseType: 'text' });
  }
}