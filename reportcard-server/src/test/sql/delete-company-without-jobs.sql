-- delete company with no jobs

use reportcard;
set @companyName = 'company1';

delete from branch
where repo_fk in (
    select repo_id from repo
    where org_fk in (
        select org_id from org
        where company_fk in
              (
                  select company_id from company
                  where company_name = @companyName
              )
    )
);

delete from repo
where org_fk in (
    select org_id from org
    where company_fk in
          (
              select company_id from company
              where company_name = @companyName
          )
);

delete from org
where company_fk in
      (
          select company_id from company
          where company_name = @companyName
      );


delete FROM reportcard.company
where company_name = @companyName;

