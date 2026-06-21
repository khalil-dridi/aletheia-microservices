# Design System ALETHEIA – Choix UI/UX

## Objectif
Dark mode moderne et cohérent avec la sidebar violette, style SaaS premium (Linear / Stripe / Notion).

## Variables (`theme.scss`)

- **--primary / --primary-light / --primary-dark** : alignés avec le violet de la sidebar pour boutons, liens et focus. En dark, violet plus lumineux (#a78bfa) pour contraste WCAG AA.
- **--bg-main** : fond du contenu principal. En dark (#1a1625), légèrement plus clair que la sidebar pour hiérarchie et profondeur.
- **--bg-card / --bg-input** : cartes et champs distincts du fond (--bg-card #221d32, --bg-input #1e1b2e en dark) avec bordures subtiles.
- **--text-primary / --text-secondary** : contraste lisible (primary #f8f8fa, secondary #b8b2c6 en dark).
- **--focus-ring** : halo violet au focus pour accessibilité et cohérence avec la sidebar.
- **--shadow-card** : ombres douces pour séparer les cartes du fond sans surcharger.
- **--transition: 0.15s ease** : transitions courtes et fluides sur tous les états interactifs.

## Inputs
- **44px** de hauteur (touch-friendly, WCAG).
- **border-radius: 10px** (--radius-input) pour un rendu moderne.
- **Focus** : bordure primary + box-shadow (--focus-ring) bien visible.
- **Hover** : bordure primary-light.
- **Error** : --danger + halo léger (aria-invalid).
- **Placeholder** : --text-secondary avec opacity 0.85 pour lisibilité.

## Cartes
- **border-radius: 16px** (--radius-card), padding cohérent, --shadow-card.
- Hover : --shadow-card-hover pour feedback visuel léger.

## Boutons
- **Primary** : dégradé --primary → --primary-dark, hover avec translateY(-2px) et ombre renforcée.
- **Secondary / Outline** : --bg-input et --border-color, hover avec léger lift (-2px) et lien vers primary pour outline.

## Responsive
- Sidebar fixe en desktop ; contenu principal scrollable.
- Mobile : grille profil en une colonne, boutons pleine largeur, espacements conservés.

## Accessibilité
- Focus visible (outline / box-shadow) sur tous les contrôles.
- Contraste texte/fond respectant WCAG AA.
- Transitions 0.15s pour ne pas gêner les préférences motion.
