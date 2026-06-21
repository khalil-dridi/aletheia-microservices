import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { of } from 'rxjs';

import { ListUserComponent } from './list-user.component';
import { UserService } from 'src/app/core/services/user.service';
import { PageUser, User } from 'src/app/core/models/user.model';

describe('ListUserComponent', () => {
  let component: ListUserComponent;
  let fixture: ComponentFixture<ListUserComponent>;
  let userServiceStub: jasmine.SpyObj<Pick<UserService, 'getUsers' | 'toggleUserStatus' | 'deleteUser'>>;

  const emptyPage: PageUser = {
    content: [],
    totalElements: 0,
    totalPages: 0,
    size: 10,
    number: 0
  };

  beforeEach(() => {
    userServiceStub = jasmine.createSpyObj('UserService', [
      'getUsers',
      'toggleUserStatus',
      'deleteUser'
    ]);
    userServiceStub.getUsers.and.returnValue(of(emptyPage));
    userServiceStub.toggleUserStatus.and.returnValue(of(''));
    userServiceStub.deleteUser.and.returnValue(of(''));

    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [ListUserComponent],
      providers: [{ provide: UserService, useValue: userServiceStub }]
    });
    fixture = TestBed.createComponent(ListUserComponent);
    component = fixture.componentInstance;
    spyOn(component, 'loadUsers').and.stub();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update enabled on the user row after toggle succeeds', () => {
    const u: User = {
      id: 1,
      email: 'x@test.com',
      role: 'LEARNER',
      nom: 'Nom',
      prenom: 'Pre',
      enabled: true
    };
    component.users = [u];
    component.toggleStatus(u);
    expect(component.users.length).toBe(1);
    expect(component.users[0].enabled).toBe(false);
    expect(userServiceStub.toggleUserStatus).toHaveBeenCalledWith(1);
  });

  it('should remove user from the local array after delete succeeds', () => {
    const u: User = {
      id: 2,
      email: 'y@test.com',
      role: 'LEARNER',
      nom: 'Nom',
      prenom: 'Pre',
      enabled: true
    };
    component.users = [u];
    component.totalElements = 1;
    component.totalPages = 1;
    component.page = 0;
    component.pendingDeleteUser = u;
    component.deleteUser(2);
    expect(component.users.length).toBe(0);
    expect(component.totalElements).toBe(0);
    expect(component.pendingDeleteUser).toBeNull();
    expect(userServiceStub.deleteUser).toHaveBeenCalledWith(2);
  });
});
