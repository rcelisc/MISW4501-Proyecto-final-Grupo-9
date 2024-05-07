import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(): boolean {
    const role = localStorage.getItem('userRole');
    // Check if the current route is allowed for the user's role
    // This logic depends on how you manage routes and roles
    if (this.isRouteAllowedForRole(role)) {
      return true;
    } else {
      this.router.navigate(['/unauthorized']);
      return false;
    }
    if (role === 'event_organizer') {
      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }

  private isRouteAllowedForRole(role: string | null): boolean {
    // Implement the logic to check if the route is allowed for the given role
    // This might involve checking against a list of allowed roles for the current route
    return true; // Placeholder: implement based on your routing and role setup
  }
}
