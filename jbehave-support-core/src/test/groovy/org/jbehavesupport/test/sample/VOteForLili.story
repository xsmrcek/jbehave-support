Narrative:
Vote for Lilien
Scenario: Vote for Lilien

Given the value [<value_A>] is saved as [ASD]

Given [https://jihlavsky.denik.cz/prave-jsme-se-narodili/prave-narozena-miminka-porodnice-kraj-vysocina-042025.html] url is open

Then execution waits for 12 seconds

When on [vote] page these actions are performed:
| element | action | data                           |
| souhlas  | CLICK  |                                |

Then execution waits for 15 seconds

When on [vote] page these actions are performed:
| element | action | data                           |
| lili  | SCROLL_ON   |                                |

Then execution waits for 3 seconds

Then on [vote] page wait until [lili] is clickable

When on [vote] page these actions are performed:
| element | action | data                           |
| lili  | CLICK   |                                |
| lili  | CLICK   |                                |

Then execution waits for {CP:ASD} seconds

Examples:
| value_A |
| 305120 |
| 308741 |
| 302125 |
| 304987 |
| 302142 |
| 302142 |
| 307310 |
| 306259 |
| 304173 |
| 306787 |
| 306714 |
| 304089 |
| 303600 |
| 307433 |
| 300876 |
| 304162 |
| 300094 |
| 302525 |
| 301568 |
| 304678 |
| 302078 |
| 309591 |
| 302318 |
| 308640 |
| 304913 |
| 308205 |
| 302572 |
| 301936 |
| 307604 |
| 303106 |
| 309661 |
| 301099 |
| 308102 |
| 308102 |
| 308105 |
| 300094 |
| 302525 |
| 301568 |
| 304678 |
| 302078 |
| 309591 |
| 302318 |
| 308640 |
| 304913 |
| 308205 |
| 302572 |
| 301936 |
| 307604 |
| 303106 |
| 309661 |
| 301099 |
| 308102 |
| 308102 |
| 308105 |
| 307433 |
| 300876 |
| 304162 |
| 300094 |
| 302525 |
| 301568 |
| 304678 |
| 302078 |
| 309591 |
| 302318 |
| 308640 |
| 302125 |
| 304987 |
| 302142 |
| 302142 |
| 307310 |
| 306259 |
| 304173 |
| 306787 |
| 306714 |
| 304089 |
| 303600 |
| 307433 |
| 300876 |
| 304162 |
| 300094 |
| 302525 |
| 301568 |
| 304678 |
| 302078 |
| 309591 |
| 302318 |
| 308640 |
| 304913 |
| 308205 |
| 302572 |
| 301936 |
| 307604 |
| 303106 |
| 309661 |
| 301099 |
| 308102 |
| 308102 |
| 308105 |
| 300094 |
| 302525 |
| 301568 |
| 304678 |
| 302078 |
| 309591 |
| 302318 |
| 308640 |
| 304913 |
| 308205 |
| 302572 |
| 301936 |
| 307604 |
| 303106 |
| 309661 |
| 301099 |
| 308102 |
| 308102 |
| 308105 |
| 307433 |
| 300876 |
| 304162 |
| 300094 |
| 302525 |
| 301568 |
| 304678 |
| 302078 |
| 309591 |
| 302318 |
| 308640 |