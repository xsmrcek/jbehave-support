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

Then execution waits for 13 seconds

When on [vote] page these actions are performed:
| element | action | data                           |
| lili  | CLICK   |                                |
| lili  | CLICK   |                                |

Then execution waits for {CP:ASD} seconds

Examples:
| value_A |
| 55120 |
| 58741 |
| 32125 |
| 64987 |
| 12142 |
| 42142 |
| 57310 |
| 66259 |
| 74173 |
| 66787 |
| 56714 |
| 54089 |
| 63600 |
| 57433 |
| 20876 |
| 44162 |
| 20094 |
| 62525 |
| 21568 |
| 24678 |
| 52078 |
| 69591 |
| 32318 |
| 28640 |
| 44913 |
| 68205 |
| 22572 |
| 31936 |
| 27604 |
| 73106 |
| 69661 |
| 71099 |
| 18102 |
| 38102 |
| 68105 |
| 20094 |
| 62525 |
| 21568 |
| 24678 |
| 52078 |
| 69591 |
| 32318 |
| 28640 |
| 44913 |
| 68205 |
| 22572 |
| 31936 |
| 27604 |
| 73106 |
| 69661 |
| 71099 |
| 18102 |
| 38102 |
| 68105 |