import {Injectable} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class GoogleAnalyticsService {

  constructor(private router: Router) {
    if (environment.googleAnalyticsEnabled && environment.googleAnalyticsId) {
      this.initializeGoogleAnalytics();
      this.subscribeToEvents();
    }
  }

  initializeGoogleAnalytics() {
    (function (i, s, o, g, r, a?, m?) {
      i['GoogleAnalyticsObject'] = r;
      i[r] = i[r] || function () {
        (i[r].q = i[r].q || []).push(arguments);
      }, i[r].l = 1 * <any>new Date();
      a = s.createElement(o),
        m = s.getElementsByTagName(o)[0];
      a.async = 1;
      a.src = g;
      m.parentNode.insertBefore(a, m);
    })(window, document, 'script',  '//www.google-analytics.com/analytics.js', 'ga');

    (<any>window).ga('create', environment.googleAnalyticsId, 'auto');
  }

  subscribeToEvents() {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.sendPageViewEvent(event.urlAfterRedirects);
      }
    });
  }

  sendPageViewEvent(newUrl: string) {
    (<any>window).ga('set', 'page', newUrl);
    (<any>window).ga('send', 'pageview');
  }

}
