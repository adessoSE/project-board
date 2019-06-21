import { Injectable } from "@angular/core";
import { NavigationEnd, Router } from "@angular/router";
import { environment } from "../../environments/environment";

declare let ga: Function;

@Injectable({
  providedIn: "root"
})
export class GoogleAnalyticsService {
  constructor(private router: Router) {
    if (environment.googleAnalyticsEnabled) {
      (function(i, s, o, g, r, a, m) {
        i["GoogleAnalyticsObject"] = r;
        (i[r] =
          i[r] ||
          function() {
            (i[r].q = i[r].q || []).push(arguments);
          }),
          (i[r].l = new Date().getTime());
        (a = s.createElement(o)), (m = s.getElementsByTagName(o)[0]);
        a.async = 1;
        a.src = g;
        m.parentNode.insertBefore(a, m);
      });
      ga("create", environment.googleAnalyticsId, "auto");

      router.events.subscribe(event => {
        if (event instanceof NavigationEnd) {
          (<any>window).ga("set", "page", event.urlAfterRedirects);
          (<any>window).ga("send", "pageview");
        }
      });
    }
  }
}
