import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CvService {

  private readonly apiUrl = `${environment.apiUrl}/cv`;

  constructor(private http: HttpClient) {}

  analyzeCV(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(`${this.apiUrl}/analyze`, formData, {
      responseType: 'text'
    });
  }

  /** Télécharge le fichier depuis l’URL du CV puis appelle l’analyse. */
  analyzeCvFromUrl(cvUrl: string): Observable<string> {
    const url = this.resolveCvUrl(cvUrl);
    return this.http.get(url, { responseType: 'blob' }).pipe(
      switchMap((blob) => {
        const segment = url.split('/').filter(Boolean).pop() || 'cv.pdf';
        const name = decodeURIComponent(segment.split('?')[0]);
        const file = new File([blob], name, {
          type: blob.type || 'application/pdf'
        });
        return this.analyzeCV(file);
      })
    );
  }

  private resolveCvUrl(cvUrl: string): string {
    const trimmed = cvUrl.trim();
    if (!trimmed) {
      return trimmed;
    }
    if (/^https?:\/\//i.test(trimmed)) {
      return trimmed;
    }
    const root = environment.apiUrl.replace(/\/api\/?$/, '');
    return trimmed.startsWith('/') ? `${root}${trimmed}` : `${root}/${trimmed}`;
  }
}
