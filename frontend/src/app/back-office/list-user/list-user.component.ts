import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  HostListener,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';
import { PageUser, User } from 'src/app/core/models/user.model';
import { UserService } from 'src/app/core/services/user.service';
import { Subject } from 'rxjs';
import { debounceTime, finalize, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-list-user',
  templateUrl: './list-user.component.html',
  styleUrls: ['./list-user.component.css']
})
export class ListUserComponent implements OnInit, OnDestroy {
  users: User[] = [];
  loading = false;
  error = '';
  selectedUser: User | null = null;
  pendingDeleteUser: User | null = null;

  /** Tracks toggle API calls per user so buttons stay aligned with backend until success */
  toggleLoadingIds: Record<number, boolean> = {};
  deleteInProgress = false;

  feedbackMessage = '';
  feedbackType: 'success' | 'error' = 'success';
  private feedbackClearHandle: ReturnType<typeof setTimeout> | null = null;

  readonly skeletonSlots = [0, 1, 2, 3, 4];
  readonly defaultAvatarUrl = 'https://cdn-icons-png.flaticon.com/512/149/149071.png';

  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;

  /** Passed to API as `q` (debounced). */
  searchQuery = '';

  /** Role filter (server-side); empty = all roles. */
  filterRole: '' | 'ADMIN' | 'LEARNER' | 'INSTRUCTOR' = '';

  readonly defaultSort = 'nom,asc';
  /** Spring Data sort parameter, e.g. `nom,asc`, `createdAt,desc`. */
  selectedSort = 'nom,asc';

  readonly sortOptions: { value: string; label: string }[] = [
    { value: 'nom,asc', label: 'Name A–Z' },
    { value: 'nom,desc', label: 'Name Z–A' },
    { value: 'email,asc', label: 'Email A–Z' },
    { value: 'createdAt,desc', label: 'Newest first' },
    { value: 'createdAt,asc', label: 'Oldest first' }
  ];

  filterPanelOpen = false;

  @ViewChild('filterWrap') filterWrap?: ElementRef<HTMLElement>;

  private readonly destroy$ = new Subject<void>();
  private readonly searchInput$ = new Subject<void>();

  constructor(
    private userService: UserService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.searchInput$
      .pipe(debounceTime(400), takeUntil(this.destroy$))
      .subscribe(() => {
        this.page = 0;
        this.loadUsers();
      });
    this.loadUsers();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.clearFeedbackTimer();
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.filterPanelOpen) {
      this.filterPanelOpen = false;
      return;
    }
    if (this.pendingDeleteUser && !this.deleteInProgress) {
      this.closeDeleteConfirm();
    } else if (this.selectedUser && !this.deleteInProgress) {
      this.closeUserDetails();
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.filterPanelOpen || !this.filterWrap) {
      return;
    }
    const t = event.target;
    if (t instanceof Node && this.filterWrap.nativeElement.contains(t)) {
      return;
    }
    this.filterPanelOpen = false;
  }

  get hasActiveFilters(): boolean {
    const sortNonDefault = this.selectedSort !== this.defaultSort;
    return !!this.searchQuery.trim() || !!this.filterRole || sortNonDefault;
  }

  isToggleLoading(user: User): boolean {
    return !!this.toggleLoadingIds[user.id];
  }

  onSearchInput(): void {
    this.searchInput$.next();
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.page = 0;
    this.loadUsers();
  }

  toggleFilterPanel(): void {
    this.filterPanelOpen = !this.filterPanelOpen;
  }

  applyFilters(): void {
    this.page = 0;
    this.filterPanelOpen = false;
    this.loadUsers();
  }

  resetFilters(): void {
    this.searchQuery = '';
    this.filterRole = '';
    this.selectedSort = this.defaultSort;
    this.page = 0;
    this.filterPanelOpen = false;
    this.loadUsers();
  }

  toggleStatus(user: User): void {
    if (!user || this.isToggleLoading(user)) {
      return;
    }

    const prevEnabled = user.enabled;
    const nextEnabled = !user.enabled;

    // UI immédiat (ne dépend pas du GET ni du corps JSON de la réponse PUT)
    this.applyToggleLocally(user.id, nextEnabled);
    this.cdr.detectChanges();

    this.setToggleLoading(user.id, true);

    this.userService
      .toggleUserStatus(user.id)
      .pipe(finalize(() => this.setToggleLoading(user.id, false)))
      .subscribe({
        next: () => {
          const label = nextEnabled ? 'enabled' : 'disabled';
          this.showFeedback(`User ${label}.`, 'success');
        },
        error: (err) => {
          console.error('toggleUserStatus failed', err);
          this.applyToggleLocally(user.id, prevEnabled);
          this.cdr.detectChanges();
          this.showFeedback('Could not update user status.', 'error');
        }
      });
  }

  loadUsers(): void {
    this.loading = true;
    this.error = '';

    this.userService
      .getUsers({
        page: this.page,
        size: this.size,
        q: this.searchQuery.trim() || undefined,
        role: this.filterRole || undefined,
        sort: this.selectedSort
      })
      .subscribe({
      next: (res: PageUser) => {
        if (res.content.length === 0 && this.page > 0 && res.totalElements > 0) {
          this.page--;
          this.loadUsers();
          return;
        }
        this.users = res.content;
        this.totalPages = res.totalPages;
        this.totalElements = res.totalElements;
        this.syncSelectedUserFromList();
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement users', err);
        this.error = 'Impossible de charger les utilisateurs.';
        this.loading = false;
      }
    });
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadUsers();
    }
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadUsers();
    }
  }

  openUserDetails(user: User): void {
    this.selectedUser = user;
  }

  closeUserDetails(): void {
    if (this.deleteInProgress) {
      return;
    }
    this.selectedUser = null;
  }

  openDeleteConfirm(user: User): void {
    if (this.deleteInProgress) {
      return;
    }
    this.pendingDeleteUser = user;
  }

  closeDeleteConfirm(): void {
    if (this.deleteInProgress) {
      return;
    }
    this.pendingDeleteUser = null;
  }

  deleteUser(id: number): void {
    const target = this.pendingDeleteUser;
    if (!target || target.id !== id || this.deleteInProgress) {
      return;
    }

    const snapshot = {
      users: this.users.map((u) => ({ ...u })),
      totalElements: this.totalElements,
      totalPages: this.totalPages,
      page: this.page
    };

    if (this.selectedUser?.id === id) {
      this.selectedUser = null;
    }
    this.pendingDeleteUser = null;

    this.applyDeleteLocally(id);
    this.cdr.detectChanges();

    this.deleteInProgress = true;
    this.userService
      .deleteUser(id)
      .pipe(finalize(() => (this.deleteInProgress = false)))
      .subscribe({
        next: () => {
          this.showFeedback('User deleted.', 'success');
          if (this.users.length === 0 && this.totalElements > 0) {
            this.loadUsers();
          }
        },
        error: (err) => {
          console.error('deleteUser failed', err);
          this.users = snapshot.users.map((u) => ({ ...u }));
          this.totalElements = snapshot.totalElements;
          this.totalPages = snapshot.totalPages;
          this.page = snapshot.page;
          this.cdr.detectChanges();
          this.showFeedback('Could not delete user.', 'error');
        }
      });
  }

  dismissFeedback(): void {
    this.clearFeedbackTimer();
    this.feedbackMessage = '';
  }

  private syncSelectedUserFromList(): void {
    if (!this.selectedUser) {
      return;
    }
    const match = this.users.find((u) => u.id === this.selectedUser!.id);
    this.selectedUser = match ?? null;
  }

  private applyToggleLocally(userId: number, enabled: boolean): void {
    this.users = this.users.map((u) => (u.id === userId ? { ...u, enabled } : u));
    if (this.selectedUser?.id === userId) {
      this.selectedUser = { ...this.selectedUser, enabled };
    }
  }

  private applyDeleteLocally(id: number): void {
    if (!this.users.some((u) => u.id === id)) {
      return;
    }
    this.users = this.users.filter((u) => u.id !== id);
    this.totalElements = Math.max(0, this.totalElements - 1);
    const pages = this.totalElements > 0 ? Math.ceil(this.totalElements / this.size) : 1;
    this.totalPages = Math.max(1, pages);
    if (this.page >= this.totalPages) {
      this.page = Math.max(0, this.totalPages - 1);
    }
    this.cleanupToggleTracking(id);
  }

  private setToggleLoading(userId: number, loading: boolean): void {
    const next = { ...this.toggleLoadingIds };
    if (loading) {
      next[userId] = true;
    } else {
      delete next[userId];
    }
    this.toggleLoadingIds = next;
  }

  private cleanupToggleTracking(userId: number): void {
    if (!this.toggleLoadingIds[userId]) {
      return;
    }
    const next = { ...this.toggleLoadingIds };
    delete next[userId];
    this.toggleLoadingIds = next;
  }

  private showFeedback(message: string, type: 'success' | 'error'): void {
    this.clearFeedbackTimer();
    this.feedbackMessage = message;
    this.feedbackType = type;
    this.feedbackClearHandle = setTimeout(() => this.dismissFeedback(), 4500);
  }

  private clearFeedbackTimer(): void {
    if (this.feedbackClearHandle !== null) {
      clearTimeout(this.feedbackClearHandle);
      this.feedbackClearHandle = null;
    }
  }
}
