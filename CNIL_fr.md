# Récolter un consentement valide
__en attente de validation par la CNIL, régulateur français de la vie privée__

Implémenter la CMP proposée par Fidzup, ne suffit pas à avoir une solution conforme, il convient égalemnt de mettre un certains nombre d'éléments en avant. Ce document a pout but de vous lister les étapes pour rendre votre récolte de consentement parfaitement valide.

## Faire une demande claire et facilement compréhensible.

Lorsque votre application démarre, il est nécessaire de faire une demande de consentement pour les traitements le requiérant selon le [réglement RGPD](https://eur-lex.europa.eu/legal-content/fr/TXT/?uri=CELEX:32016R0679).
Pour cela, il va falloir suivre les recommendations édictées par le RGPD, et le plus simple est de suivre les recommendations émises par le groupement des autorités de controle européennes. Vous pourrez trouver ces recommendations sur le site de le commission européenne en suivant [ce lien](http://ec.europa.eu/newsroom/article29/item-detail.cfm?item_id=623051)

Toutefois, il faut garder certains détails à l'esprit. Tout d'abord, la manière la plus simple, serait de créer une fenêtre popup pour la demande de consentement, par responsable de traitement. Solution idéeale. Maintenant si vous disposez de plusieurs partenaires, on se rend vite compte que l'empilement de popup, même si respectueux du réglement, n'est respecte pas l'esprit. Le consentement pourrait certes être valide pour le premier responsable cité, mais plus probablement automatique sur un clic sur une 6ièmem poup.

  Nous avons donc faire le choix de ne garder qu'une seule popup dans ce document. Parce que c'est la solution qui nous apparait comme offrant le plus de clarté à l'utilisateur final quand au à l'utilisation de ses données, et de l'information nécessaire qu'il doit trouver préalablement à l'utilisation d'une application. Evidemment, ce choix peut se discuter si vous n'avez que 2 ou 3 partenaires. Mais cette CMP a été écrite avec en tête les éditeurs avec lesquels la société Fidzup. Editeurs qui ont tous plus de 10 partenaires.

## La popup

Exemple de popup qui convient pour la récolte de données géolocalisée à des fins publicitaires:

On peut décomposer cette popup en trois éléments distincts.

### La description

Tout d'abord un texte qui doit décrire précisément pourquoi nous avons besoin du consentement de l'utilisateurs. Dans l'exemple ci dessus Nous décrivons précisément les données récoltées (identifiant publicitaire, localisation GPS, adresse MAC Wifi ou le bluetooth), pour associer au téléphone de l'utilisateur une position géographique. Nous décrivons ensuite à quoi vont servir ces données, le but (publicité ciblée géo-localisée). Nous pouvons également voir une phrase qui cite sommairement les droits de l'utilisateur, mais surtout qui permet de mettre un lien vers la politique de confientialité de l'éditeur. Politique de confidentialité qui se doit d'être complète concernant les données de l'éditeurs (décrivant clairement le type de donnée récoltées, les durées de conservation de ces données, les droits de l'utilisateurs aisni que le moyen de l'exercer, etc..), mais aussi lister de manière exhaustives l'ensemble des partenaires, et mettre un lien vers la politique de confidentialité de chacun des partenaires.

### Les boutons

Une seconde partie de la popup contient 3 boutons. Le premier bouton, permet d'accepter toutes les finalités et tous les partenaires d'un seul coup. Le second bouton permet de refuser toutes les finalités et tous les partenaires d'un seul coup également. Le troisièrem bouton, permet quand à lui de se diriger vers une autre fenêtre listant les finalités et ayant un lien vers les partenaires. Ce dernier bouton a pour but de permettre à l'utilisateur de choisir les finalités et/ou les partenaires avec lesquels ils acceptent de donner son consentement. (nous appelerons ce choix, consentement à la carte)

### Lien vers les partenaires

Enfin cette poup doit se terminer par un lien direct vers la liste des partenaires. L'utilisateur doit être capable de visualiser rapidement avec tous les responsables de traitement pour lesquels il va donner ou refuser son consentement.

## La liste des finalités

Exemple de liste des finalités

La fenêtre est répartie en 3 zones. La première concerne les finalités propre à l'éditeur de l'application. La seconde concerne les finalités qui sont associées à la liste des partenaires. Et enfin la troisième zone est un lien vers la liste des partenaires.

Pour chacune de ces finalités, il est possible de cliquer dessus pour avoir un descriptif détaillé. Il est important pour apporter de la clarté à l'utilisateur qu'il puisse sélectionner et déselectionner les finalités une à une sur cette page. Certaines autres solutions proposent de ne pouvoir sélectionner ou déselectionner les finalités qu'après être entré dans une troisième fenêtre, propre à la fonctionnalité. Cette solution a le mérite de promouvoir un choix éclairé sur la fonctionnalité (puisque le texte descriptif est alors toujours affiché). Nous n'avons pas fait ce choix, parce qu'il nous apparait plus important pour la clarté de l'information à l'utilisateur, de pouvoir choisir de sélectionner ou déselectionner les finalité sur la base de leur nom, que de forcer ce dernier à lire le descriptif détaillé avant de faire son choix. Le fait de devoir entrer dans plusieures sous fenêtres pour retirer ou accepter son consentement, nous semble en désaccord avec les recommendations du G29 européen (vous trouverez un lien vers ces recommendation en début de document).

## La liste des partenaires

Exemple de liste des partenaires

Cette liste est facile à comprendre, et contient le nom des responsables de traitement, ainsi que la possibilité de donner ou refuser son consentement à chacun d'entre eux. Un clic sur une ligne, vous emmêne vers une sous fenêtre qui vous liste les finalités exactes poursuivies par ce partenaires et qui mets à disposition un lien vers la politique de confidentialité de ce partenaire.

## Quand afficher la CMP

Il est certes nécessaire que l'utilisateur ait accès à cette popup dès le chargement initiale de l'application, mais pas seulement. Cette popup doit également être ré-affiché lorsqu'une ou plusieures finalités changent, sont ajoutés ou su^pprimées. De même il faut prévoir dans un menu de l'application une manière de réafficher cette popup, pour donner à l'utilisateur le droit à ajouter ou retirer son consentement.

## Pourquoi ajouter une 6ième finalité à a liste établie par l'IAB ?

L'autorité française de controle de la vie privée, estime que l'utilisation de la géo-localisation des utilisateurs n'est pas simplement une fonctionnalité liée au ciblage publicitaire. Face au risque apporté à la vie privée lors de la récolte de cette information, elle considère qu'il s'agit d'une finalité à part entière. Nous sommes en contact avec l'IAB pour l'ajout de cette finalité dans leur framework de consentement, mais le respect de la loi nous impose de prendre en considération dès aujourd'hui la position du régulateur.

## J'ai implémenté la CMP Fidzup, suivi toutes les recommendations de ce document, je suis donc 100% RGPD compliant !

Il est vrai que si vous avez suivi toutes les recommendations de ce document, et implémenté la CMP Fidzup, votre __récolte du constement__ est 100% RGPD compliant. Mais n'oubiez pas que le traitement dans sa globalité, demande encore quelques efforts. Vous devez avoir établi une ficher de registre de ce traitement, contenant toutes les informations nécessaires, dans votre [registre de traitement](https://www.cnil.fr/fr/RGDP-le-registre-des-activites-de-traitement). De même, il faut prendre soin d'avoir une politique de confidentialité claire.

Enfin, n'oubliez pas qu'une analyse de risque n'est pas à exclure. Si vous travaillez dans le domaine publicitaire en utilisant la géolocalisation, [Fidzup](https://www.fidzup.com/) est en train de finaliser une analyse d'impact complète, qu'elle compte proposer à ses partenaires.

## Auteur

Nicolas Blanc - DPO chez Fidzup
