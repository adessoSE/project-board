import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class IsBossResolverService implements Resolve<boolean> {

  constructor(private authenticationService: AuthenticationService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    return this.authenticationService.isBoss;
  }
}