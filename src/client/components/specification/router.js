import BaseWithProject from "./BaseWithProject";
import ProjectMain from "./ProjectMain";
import featuresRouter from "./features/features.router";
import datamodelRouter from "./datamodel/datamodel.router";
import staticsRouter from "./statics/statics.router";

const routes = [
  {
    path: "/:pName/:pVersion",
    component: BaseWithProject,
    children: [].concat(
      [
        {
          name: "ProjectMain",
          path: "",
          component: ProjectMain,
        },
      ],
      featuresRouter,
      datamodelRouter,
      staticsRouter
    ),
  },
];

export default routes;
